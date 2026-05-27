package com.zcard.feature.cardeditor.textedit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextFontFamily
import com.zcard.domain.model.TextAlignment
import com.zcard.domain.model.TextAttributes
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.usecase.SaveTextElementsParams
import com.zcard.domain.usecase.SaveTextElementsUseCase
import com.zcard.feature.R
import com.zcard.feature.cardeditor.model.TempText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TextEditViewModel"

@HiltViewModel
class TextEditViewModel @Inject constructor(
    private val unityBridge: UnityBridge,
    private val cardElementRepository: CardElementRepository,
    private val saveTextElementsUseCase: SaveTextElementsUseCase,
) : ViewModel() {

    private var cardId: Long? = null
    private var observeTextsJob: Job? = null
    private val pendingDeleteIds: MutableSet<Long> = mutableSetOf()

    private val _textEditState = MutableStateFlow(TextEditState())
    val textEditState: StateFlow<TextEditState> = _textEditState

    private val _textEditSideEffect = Channel<TextEditSideEffect>(Channel.BUFFERED)
    val textEditSideEffect = _textEditSideEffect.receiveAsFlow()

    fun onIntent(intent: TextEditIntent) {
        when (intent) {
            is TextEditIntent.Init -> handleInit(intent.cardId)
            is TextEditIntent.ImeVisible -> handleImeVisible(intent.visible)
            is TextEditIntent.ResetCamera -> handleResetCamera()
            is TextEditIntent.MissingTextSelection -> handleMissingTextSelection()
            is TextEditIntent.AddText -> handleAddText()
            is TextEditIntent.DeleteText -> handleDeleteText(intent.tempId)
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
        observeTextsJob?.cancel()
        observeTextsJob = cardElementRepository.getTextElementsFlowByCardId(cardId).onEach { result ->
            result.onSuccess { texts ->
                val tempTexts = texts.map { element -> TempText(textElement = element) }.asReversed()
                this@TextEditViewModel.cardId = cardId
                _textEditState.update {
                    it.copy(
                        savedTexts = tempTexts,
                        tempTexts = tempTexts,
                        selectedTextTempId = tempTexts.firstOrNull()?.tempId
                    )
                }

                unityBridge.clearAllTexts()
                tempTexts.forEach { unityBridge.createText(it.tempId, it.textElement) }
            }.onFailure { e ->
                Log.e(TAG, "observeTexts: $e")
                _textEditSideEffect.trySend(
                    TextEditSideEffect.ToastMessage(R.string.text_edit_msg_fail_load)
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun handleImeVisible(visible: Boolean) {
        _textEditState.update {
            it.copy(
                unityContainerHeightFraction = if(visible) 0.4f else 0.6f
            )
        }
    }

    private fun handleMissingTextSelection() {
        _textEditState.update { it.copy(selectedTextTempId = it.tempTexts.firstOrNull()?.tempId) }
    }

    private fun handleAddText() {
        val newText = TempText()
        _textEditState.update {
            it.copy(
                tempTexts = listOf(newText) + it.tempTexts,
                selectedTextTempId = newText.tempId
            )
        }

        unityBridge.createText(
            tempId = newText.tempId,
            textElement = newText.textElement
        )
    }

    private fun handleDeleteText(tempId: Long) {
        val oldList = _textEditState.value.tempTexts
        val idx = oldList.indexOfFirst { it.tempId == tempId }.takeIf { it >= 0 } ?: return
        val selectedText = oldList[idx]
        val newSelected = oldList.getOrNull(idx - 1) ?: oldList.getOrNull(idx + 1)

        _textEditState.update {
            it.copy(
                tempTexts = it.tempTexts - selectedText,
                selectedTextTempId = newSelected?.tempId
            )
        }

        selectedText.textElement.elementId?.let { pendingDeleteIds.add(it) }

        unityBridge.deleteObject(selectedText.tempId)
    }

    private fun handleSelectText(textId: Long) {
        _textEditState.update { it.copy(selectedTextTempId = textId) }
        unityBridge.selectObject(textId)
    }

    private fun handleChangeTextContent(newText: String) {
        val textId = _textEditState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(content = newText) }
        unityBridge.updateTextContent(textId, newText)
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
            val newList = it.tempTexts.map { item ->
                if(item.tempId == textId) {
                    val newText = item.textElement.copy(
                        posX = item.textElement.posX + direction.dx,
                        posY = item.textElement.posY + direction.dy,
                        posZ = item.textElement.posZ + direction.dz
                    )
                    unityBridge.updatePosition(textId, newText.posX, newText.posY, newText.posZ)
                    item.copy(textElement = newText)
                } else item
            }
            it.copy(tempTexts = newList)
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
        saveTextChanges(onSuccess = ::exitTextEditor)
    }

    private fun handleDiscardAndExit() {
        unityBridge.clearAllTexts()
        _textEditState.value.savedTexts.forEach { unityBridge.createText(it.tempId, it.textElement) }
        exitTextEditor()
    }

    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    // ── 공통 유틸 ─────────────────────────────────────────────────────────────

    private fun updateTempTextAttribute(textId: Long, transform: TextAttributes.() -> TextAttributes) {
        _textEditState.update {
            val newList = it.tempTexts.map { item ->
                if(item.tempId == textId) {
                    item.copy(textElement = item.textElement.copy(attributes = item.textElement.attributes.transform()))
                } else item
            }
            it.copy(tempTexts = newList)
        }
    }

    private fun saveTextChanges(onSuccess: () -> Unit = {}) {
        val id = cardId ?: return

        viewModelScope.launch {
            saveTextElementsUseCase(
                SaveTextElementsParams(
                    cardId = id,
                    updates = _textEditState.value.tempTexts.map { it.textElement },
                    deleteIds = pendingDeleteIds.toSet()
                )
            ).onSuccess { result ->
                if(result.failedUpdates.isNotEmpty() || result.failedDeleteIds.isNotEmpty()) {
                    _textEditSideEffect.trySend(TextEditSideEffect.ToastMessage(R.string.text_edit_msg_partial_fail_save))
                }
                pendingDeleteIds.clear()
                onSuccess()
            }.onFailure {
                _textEditSideEffect.trySend(TextEditSideEffect.ToastMessage(R.string.text_edit_msg_fail_save))
            }
        }
    }

    private fun exitTextEditor() {
        _textEditState.update { it.copy(showUnsavedChangesDialog = false) }
        _textEditSideEffect.trySend(TextEditSideEffect.Finish)
    }
}