package com.zcard.feature.cardeditor.textedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.feature.cardeditor.model.TempTextElement
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextFontFamily
import com.zcard.domain.model.TextAlignment
import com.zcard.domain.model.TextAttributes
import com.zcard.domain.model.TextElement
import com.zcard.domain.usecase.GetTextElementsUseCase
import com.zcard.domain.usecase.SaveTextElementsParams
import com.zcard.domain.usecase.SaveTextElementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long
import kotlin.onSuccess

private const val TAG = "TextEditViewModel"

@HiltViewModel
class TextEditViewModel @Inject constructor(
    private val unityBridge: UnityBridge,
    private val getTextsUseCase: GetTextElementsUseCase,
    private val saveTextElementsUseCase: SaveTextElementsUseCase,
) : ViewModel() {

    private var _cardId: Long = -1L

    private var _deletedTextElementIds: MutableSet<Long> = mutableSetOf()

    private val _textEditState = MutableStateFlow(TextEditState())
    val textEditState: StateFlow<TextEditState> = _textEditState

    private val _textEditSideEffect = Channel<TextEditSideEffect>(Channel.BUFFERED)
    val textEditSideEffect = _textEditSideEffect.receiveAsFlow()

    private val _imeVisible = MutableStateFlow(false)
    private val imeVisible = _imeVisible.asStateFlow()

    fun setImeVisible(visible: Boolean) {
        _imeVisible.value = visible
    }

    val unityContainerHeightFractionFlow = combine(_textEditState, imeVisible) { state, ime ->
        if(ime) 0.4f else 0.6f
    }

    fun onIntent(intent: TextEditIntent) {
        when (intent) {
            is TextEditIntent.Init -> handleInit(intent.cardId)
            is TextEditIntent.ResetCamera -> handleResetCamera()
            is TextEditIntent.MissingTextSelection -> handleMissingTextSelection()
            is TextEditIntent.AddText -> handleAddText()
            is TextEditIntent.DeleteText -> handleDeleteText(intent.textId)
            is TextEditIntent.SelectText -> handleSelectText(intent.textId)
            is TextEditIntent.ChangeTextContent -> handleChangeTextContent(intent.newText)
            is TextEditIntent.SelectAlignment -> handleSelectAlignment(intent.newAlignment)
            is TextEditIntent.SelectColor -> handleSelectColor(intent.newColor)
            is TextEditIntent.ChangeFontSize -> handleChangeFontSize(intent.newSize)
            is TextEditIntent.SelectFont -> handleSelectFont(intent.newFont)
            is TextEditIntent.MoveText -> handleMoveText(intent.direction)
            is TextEditIntent.Exit -> handleExit()
            is TextEditIntent.DismissDialog -> handleDismissDialog()
            is TextEditIntent.SaveChanges -> handleSaveChanges()
            is TextEditIntent.SaveAndExit -> handleSaveAndExit()
            is TextEditIntent.DiscardAndExit -> handleDiscardAndExit()
        }
    }

    private fun handleInit(cardId: Long) {
        val texts = _textEditState.value.texts
        val newList = texts.map { element ->
            TempTextElement(
                textElement = TextElement(
                    elementId = element.elementId,
                    attributes = element.attributes,
                    posX = element.posX,
                    posY = element.posY,
                    posZ = element.posZ
                )
            )
        }.asReversed()
        _textEditState.update {
            it.copy(
                tempTextList = newList,
                selectedTextTempId = newList.firstOrNull()?.tempId
            )
        }
        unityBridge.clearAllTexts()
        newList.forEach {
            unityBridge.createText(
                tempId = it.tempId,
                textElement = it.textElement
            )
        }
    }

    private fun handleMissingTextSelection() {
        if (_textEditState.value.tempTextList.isNotEmpty() && _textEditState.value.selectedTextTempId == null) {
            _textEditState.update { it.copy(selectedTextTempId = it.tempTextList.first().tempId) }
            return
        }
    }

    private fun handleAddText() {
        val newTextElement = TempTextElement()
        _textEditState.update {
            it.copy(
                tempTextList = listOf(newTextElement) + it.tempTextList,
                selectedTextTempId = newTextElement.tempId
            )
        }

        unityBridge.createText(
            tempId = newTextElement.tempId,
            textElement = newTextElement.textElement
        )
    }

    private fun handleDeleteText(textId: Long) {
        val oldList = _textEditState.value.tempTextList
        val selectedText = oldList.find { it.tempId == textId } ?: return
        val idx = oldList.indexOfFirst { it.tempId == textId }
        val newSelected = oldList.getOrNull(idx - 1) ?: oldList.getOrNull(idx + 1)

        _textEditState.update {
            it.copy(
                tempTextList = it.tempTextList - selectedText,
                selectedTextTempId = newSelected?.tempId
            )
        }

        selectedText.textElement.elementId?.let { _deletedTextElementIds.add(it) }

        unityBridge.deleteObject(selectedText.tempId)
    }

    private fun handleSelectText(textId: Long) {
        _textEditState.update { it.copy(selectedTextTempId = textId) }
        unityBridge.selectObject(textId)
    }

    private fun handleChangeTextContent(newText: String) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(content = newText) }
        unityBridge.updateTextContent(_textEditState.value.selectedTextTempId ?: 0, newText)
    }

    private fun handleSelectAlignment(newAlignment: TextAlignment) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(alignment = newAlignment) }
        unityBridge.updateTextAlign(textId, newAlignment.alignCode)
    }

    private fun handleSelectColor(newColor: TextColor) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(textColor = newColor) }
        unityBridge.updateTextColor(textId, newColor.rgbaColor)
    }

    private fun handleChangeFontSize(newSize: Float) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(fontSize = newSize) }
        unityBridge.updateFontSize(textId, newSize)
    }

    private fun handleSelectFont(newFont: TextFontFamily) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(fontFamily = newFont) }
        unityBridge.updateFont(textId, newFont.key)
    }

    private fun handleMoveText(direction: Direction) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        _textEditState.update {
            val newList = it.tempTextList.map { item ->
                if(item.tempId == textId) {
                    val newPosX = item.textElement.posX + direction.dx
                    val newPosY = item.textElement.posY + direction.dy
                    val newPosZ = item.textElement.posZ + direction.dz
                    unityBridge.updatePosition(textId, newPosX, newPosY, newPosZ)
                    item.copy(
                        textElement = item.textElement.copy(
                            posX = newPosX,
                            posY = newPosY,
                            posZ = newPosZ
                        )
                    )
                } else item
            }
            it.copy(tempTextList = newList)
        }
    }


    private fun handleExit() {
        if(_textEditState.value.hasPendingText) {
            _textEditState.update { it.copy(showUnsavedChangesDialog = true) }
        } else {
            _textEditSideEffect.trySend(TextEditSideEffect.Finish)
        }
    }

    private fun handleDismissDialog() {
        _textEditState.update { it.copy(showUnsavedChangesDialog = false) }
    }

    private fun handleSaveChanges() {
        saveTextChanges()
    }

    private fun handleSaveAndExit() {
        saveTextChanges(true)
        exitTextEditor()
    }

    private fun handleDiscardAndExit() {
        viewModelScope.launch {
            getTextsUseCase(_cardId)
                .onSuccess { result ->
                    _textEditState.update { it.copy(texts = result) }
                    unityBridge.replaceAllTexts(result)
                }
        }
        exitTextEditor()
    }

    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    // ── 공통 유틸 ─────────────────────────────────────────────────────────────

    private fun showDialog() {
        _textEditState.update { it.copy(showUnsavedChangesDialog = true) }
    }

    private fun hideDialog() {
        _textEditState.update { it.copy(showUnsavedChangesDialog = false) }
    }

    private fun updateTempTextAttribute(textId: Long, newTextAttributes: TextAttributes.() -> TextAttributes) {
        _textEditState.update {
            val newList = it.tempTextList.map { item ->
                if(item.tempId == textId) {
                    item.copy(textElement = item.textElement.copy(attributes = item.textElement.attributes.newTextAttributes()))
                } else item
            }
            it.copy(tempTextList = newList)
        }
    }

    private fun saveTextChanges(shouldFinishEditing: Boolean = false) {
        viewModelScope.launch {
            saveTextElementsUseCase(
                SaveTextElementsParams(
                    cardId = _cardId,
                    updates = _textEditState.value.tempTextList.map { it.textElement },
                    deletedIds = _deletedTextElementIds
                )
            ).onSuccess { result ->
                if(!shouldFinishEditing) {
                    _textEditState.update {
                        val mergedList =
                            (result.updatedElements + result.failedUpdates).map { text ->
                                TempTextElement(textElement = text)
                            }
                        it.copy(
                            tempTextList = mergedList,
                            selectedTextTempId = mergedList.firstOrNull()?.tempId,
                        )
                    }
                    _deletedTextElementIds = result.deletedIds.toMutableSet()
                } else {
                    _textEditState.update {
                        it.copy(texts = result.updatedElements)
                    }
                    unityBridge.replaceAllTexts(result.updatedElements)
                }
            }
        }
    }

    private fun exitTextEditor() {
        _textEditState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null, showUnsavedChangesDialog = false) }
        _textEditSideEffect.trySend(TextEditSideEffect.Finish)
        _deletedTextElementIds = mutableSetOf()
    }
}