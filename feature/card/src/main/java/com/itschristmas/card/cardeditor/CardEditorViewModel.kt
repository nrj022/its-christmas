package com.itschristmas.card.cardeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.card.cardeditor.model.PanelState
import com.itschristmas.card.cardeditor.model.TempTextElementState
import com.itschristmas.card.cardeditor.model.TempTransformState
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption
import com.itschristmas.domain.model.TextAttributes
import com.itschristmas.domain.model.TextElement
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.repository.CardRepository
import com.itschristmas.domain.usecase.UpdateTextParams
import com.itschristmas.domain.usecase.UpdateTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long

private const val TAG = "CardEditorViewModel"

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val cardElementRepository: CardElementRepository,
    private val cardRepository: CardRepository,
    private val updateTextUseCase: UpdateTextUseCase
) : ViewModel() {

    private val _cardEditorState = MutableStateFlow(CardEditorState())
    val cardEditorState: StateFlow<CardEditorState> = _cardEditorState

    private var _deletedTextElementIds: MutableSet<Long> = mutableSetOf()

    private val _imeVisible = MutableStateFlow(false)
    val imeVisible = _imeVisible.asStateFlow()

    fun setImeVisible(visible: Boolean) {
        _imeVisible.value = visible
    }

    val unityContainerHeightFractionFlow = combine(cardEditorState, imeVisible) { state, ime ->
        if(ime && state.panelState == PanelState.TEXT_EDITOR) 0.4f
        else state.unityContainerHeightFraction
    }

    fun onIntent(intent: CardEditorIntent) {
        when(intent) {
            is CardEditorIntent.Init -> {
                handleInit(intent.cardId)
            }
            is CardEditorIntent.CardTitleChanged -> {
                handleCardTitleChanged(intent.newTitle)
            }
            is CardEditorIntent.CompleteClicked -> {
                handleCompleteClicked()
            }
            is CardEditorIntent.GenerateCard -> {
                handleGenerateCard()
            }
            is CardEditorIntent.ObjectClicked -> {
                handleObjectClicked(intent.cardId, intent.clickedObject)
            }
            is CardEditorIntent.BackgroundClicked -> {
                handleBackgroundClicked(intent.assetId)
            }
            is CardEditorIntent.AddTextButtonClicked -> {
                handleAddTextButtonClicked(intent.cardId)
            }
            is CardEditorIntent.MyObjectClicked -> {
                handleMyObjectClicked(intent.element)
            }
            is CardEditorIntent.DialogStateChanged -> {
                handleDialogStateChanged(intent.dialogState)
            }
            is CardEditorIntent.DeleteMyObject -> {
                handleDeleteMyObject()
            }
            is CardEditorIntent.AdjustClicked -> {
                handleAdjustClicked()
            }
            is CardEditorIntent.AdjustCancelClicked -> {
                handleAdjustCancelClicked()
            }
            is CardEditorIntent.AdjustApplyClicked -> {
                handleAdjustApplyClicked()
            }
            is CardEditorIntent.AdjustApplyAndExit -> {
                handleAdjustApplyAndExit()
            }
            is CardEditorIntent.AdjustDiscardAndExit -> {
                handleAdjustDiscardAndExit()
            }
            is CardEditorIntent.ObjectDirectionClicked -> {
                handleObjectDirectionClicked(intent.direction)
            }
            is CardEditorIntent.ScaleChanged -> {
                handleScaleChanged(intent.newScale)
            }
            is CardEditorIntent.CameraResetClicked -> {
                handleCameraResetClicked()
            }
            is CardEditorIntent.TransformResetClicked -> {
                handleTransformResetClicked()
            }
            is CardEditorIntent.RequestDefaultText -> {
                handleDefaultTextRequest()
            }
            is CardEditorIntent.AddText -> {
                handleAddText()
            }
            is CardEditorIntent.DeleteText -> {
                handleDeleteText(intent.textId)
            }
            is CardEditorIntent.TextClicked -> {
                handleTextClicked(intent.textId)
            }
            is CardEditorIntent.TextChanged -> {
                handleTextChanged(intent.newText)
            }
            is CardEditorIntent.AlignmentSelected -> {
                handleAlignmentSelected(intent.newAlignment)
            }
            is CardEditorIntent.ColorSelected -> {
                handleColorSelected(intent.newColor)
            }
            is CardEditorIntent.FontSizeChanged -> {
                handleFontSizeChanged(intent.newSize)
            }
            is CardEditorIntent.FontSelected -> {
                handleFontSelected(intent.newFont)
            }
            is CardEditorIntent.TextDirectionClicked -> {
                handleTextDirectionClicked(intent.direction)
            }
            is CardEditorIntent.TextApplyClicked -> {
                handleTextApplyClicked(intent.cardId)
            }
            is CardEditorIntent.TextApplyAndExit -> {
                handleTextApplyAndExit(intent.cardId)
            }
            is CardEditorIntent.TextDiscardAndExit -> {
                handleTextDiscardAndExit()
            }
        }
    }

    private fun handleInit(cardId: Long) {
        getCardDataFromDB(cardId)
        getObjectsFromDB()
        getBackgroundsFromDB()

        // MyObjects Flow 구독
        loadMyObjectsFromDB(cardId)
    }

    private fun handleCardTitleChanged(newTitle: String) {
        _cardEditorState.update { it.copy(cardTitle = newTitle) }
    }

    private fun handleCompleteClicked() {
        updateDialogState(EditorDialogState.SET_CARD_TITLE)
    }

    private fun handleGenerateCard() {
        /* TODO */
    }

    private fun handleObjectClicked(cardId: Long, clickedObject: Asset) {
        viewModelScope.launch {
            cardElementRepository.insertCardElement(
                CardElement(
                    elementId = 0,
                    cardId = cardId,
                    assetId = clickedObject.assetId,
                    elementType = ElementType.OBJECT,
                    posX = 0,
                    posY = 0,
                    scale = 1
                )
            )
        }
    }

    private fun handleBackgroundClicked(assetId: Long) {
        _cardEditorState.update {
            it.copy(selectedBackground = if (it.selectedBackground == assetId) null else assetId)
        }
    }

    private fun handleAddTextButtonClicked(cardId: Long) {
        updatePanelState(PanelState.TEXT_EDITOR)
        viewModelScope.launch {
            cardElementRepository.getTextElementsByCardId(cardId)
                .onSuccess { result ->
                    val newList = result.mapNotNull { element ->
                        element.textAttributes?.let { attr ->
                            TempTextElementState(
                                textElement = TextElement(
                                    elementId = element.elementId,
                                    attributes = attr,
                                    posX = element.posX,
                                    posY = element.posY
                                )
                            )
                        }
                    }.asReversed()
                    _cardEditorState.update {
                        it.copy(
                            tempTextList = newList,
                            selectedTextTempId = newList.firstOrNull()?.tempId
                        )
                    }
                }
        }
    }

    private fun handleMyObjectClicked(element: CardElementWithAssetKeys) {
        _cardEditorState.update {
            it.copy(selectedMyObject = if (it.selectedMyObjectIdx == element.cardElement.elementId) null else element)
        }
    }

    private fun handleDialogStateChanged(dialogState: EditorDialogState) {
        updateDialogState(dialogState)
    }

    private fun handleDeleteMyObject() {
        viewModelScope.launch {
            _cardEditorState.value.selectedMyObjectIdx?.let {
                cardElementRepository.deleteCardElementById(it)
            }
            updateDialogState(EditorDialogState.NONE)
        }
    }

    private fun handleAdjustClicked() {
        _cardEditorState.value.selectedMyObject?.let { obj ->
            updatePanelState(PanelState.TRANSFORM_CONTROL)
            _cardEditorState.update {
                it.copy(
                    tempTransformState = TempTransformState(
                        elementId = obj.cardElement.elementId,
                        thumbnailKey = obj.thumbnailKey,
                        posX = obj.cardElement.posX,
                        posY = obj.cardElement.posY,
                        scale = obj.cardElement.scale
                    )
                )
            }
        }
    }

    private fun handleAdjustCancelClicked() {
        if(_cardEditorState.value.hasPendingTransform) {
            updateDialogState(EditorDialogState.UNSAVED_TRANSFORM_CHANGES)
        } else {
            exitTransform()
        }
    }

    private fun handleAdjustApplyClicked() {
        updateTransform()
    }

    private fun handleAdjustApplyAndExit() {
        updateTransform()
        exitTransform()
        updateDialogState(EditorDialogState.NONE)
    }

    private fun handleAdjustDiscardAndExit() {
        exitTransform()
        updateDialogState(EditorDialogState.NONE)
    }

    private fun handleObjectDirectionClicked(direction: Direction) {
        val tempState = _cardEditorState.value.tempTransformState ?: return
        _cardEditorState.update {
            it.copy(tempTransformState =
                tempState.copy(
                    posY = tempState.posY + direction.dy,
                    posX = tempState.posX + direction.dx
                )
            )
        }
    }

    private fun handleScaleChanged(newScale: Int) {
        if(newScale < 1) return

        _cardEditorState.update {
            it.copy(tempTransformState = it.tempTransformState?.copy(scale = newScale))
        }
    }

    private fun handleCameraResetClicked() {

    }

    private fun handleTransformResetClicked() {
        val tempState = _cardEditorState.value.tempTransformState ?: return
        val initialState = _cardEditorState.value.selectedMyObject?.cardElement

        _cardEditorState.update {
            it.copy(tempTransformState =
                tempState.copy(
                    posX = initialState?.posX ?: 0,
                    posY = initialState?.posY ?: 0,
                    scale = initialState?.scale ?: 1
                )
            )
        }
    }

    private fun handleDefaultTextRequest() {
        _cardEditorState.update {
            if (it.tempTextList.isNotEmpty()) {
                if (it.selectedTextTempId == null) {
                    it.copy(selectedTextTempId = it.tempTextList.first().tempId)
                } else it
            } else {
                val newTextElement = TempTextElementState()
                it.copy(
                    tempTextList = listOf(newTextElement),
                    selectedTextTempId = newTextElement.tempId
                )
            }
        }
    }

    private fun handleAddText() {
        val newTextElement = TempTextElementState()
        _cardEditorState.update {
            it.copy(
                tempTextList = listOf(newTextElement) + it.tempTextList,
                selectedTextTempId = newTextElement.tempId
            )
        }
    }

    private fun handleDeleteText(textId: Long) {
        val oldList = _cardEditorState.value.tempTextList
        val selectedText = oldList.find { it.tempId == textId } ?: return
        val idx = oldList.indexOfFirst { it.tempId == textId }
        val newSelected = oldList.getOrNull(idx - 1) ?: oldList.getOrNull(idx + 1)

        _cardEditorState.update {
            it.copy(
                tempTextList = it.tempTextList - selectedText,
                selectedTextTempId = newSelected?.tempId
            )
        }

        selectedText.textElement.elementId?.let { _deletedTextElementIds.add(it) }
    }

    private fun handleTextClicked(textId: Long) {
        _cardEditorState.update {
            it.copy(
                selectedTextTempId = textId
            )
        }
    }

    private fun handleTextChanged(newText: String) {
        updateTempTextAttribute { copy(content = newText) }
    }

    private fun handleAlignmentSelected(newAlignment: TextAlignmentOption) {
        updateTempTextAttribute { copy(alignment = newAlignment) }
    }

    private fun handleColorSelected(newColor: ColorOption) {
        updateTempTextAttribute { copy(textColor = newColor) }
    }

    private fun handleFontSizeChanged(newSize: Float) {
        updateTempTextAttribute { copy(fontSize = newSize) }
    }

    private fun handleFontSelected(newFont: FontOption) {
        updateTempTextAttribute { copy(fontFamily = newFont) }
    }

    private fun handleTextDirectionClicked(direction: Direction) {
        _cardEditorState.update {
            val id = it.selectedTextTempId
            val newList = it.tempTextList.map { item ->
                if(item.tempId == id) {
                    item.copy(
                        textElement = item.textElement.copy(
                            posY = item.textElement.posY + direction.dy,
                            posX = item.textElement.posX + direction.dx
                        )
                    )
                } else item
            }
            it.copy(tempTextList = newList)
        }
    }

    private fun handleTextApplyClicked(cardId: Long) {
        updateTextAttribute(cardId)
    }

    private fun handleTextApplyAndExit(cardId: Long) {
        updateTextAttribute(cardId)
        updateDialogState(EditorDialogState.NONE)
        updatePanelState(PanelState.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null) }
    }

    private fun handleTextDiscardAndExit() {
        updateDialogState(EditorDialogState.NONE)
        updatePanelState(PanelState.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null) }
    }

    private fun updatePanelState(panelState: PanelState) {
        _cardEditorState.update {
            it.copy(panelState = panelState)
        }
    }

    private fun updateDialogState(dialogState: EditorDialogState) {
        _cardEditorState.update {
            it.copy(editorDialogState = dialogState)
        }
    }

    private fun updateTransform() {
        val tempState = _cardEditorState.value.tempTransformState ?: return
        val initialState = _cardEditorState.value.selectedMyObject ?: return

        viewModelScope.launch {
            cardElementRepository.updateElementTransform(
                elementId = tempState.elementId,
                posX = tempState.posX,
                posY = tempState.posY,
                scale = tempState.scale
            )
            _cardEditorState.update {
                it.copy(selectedMyObject =
                    initialState.copy(
                        cardElement = initialState.cardElement.copy(
                            posX = tempState.posX,
                            posY = tempState.posY,
                            scale = tempState.scale
                        )
                    )
                )
            }
        }
    }

    private fun exitTransform() {
        updatePanelState(PanelState.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTransformState = null) }
    }

    private fun updateTempTextAttribute(newTextAttributes: TextAttributes.() -> TextAttributes) {
        _cardEditorState.update {
            val id = it.selectedTextTempId
            val newList = it.tempTextList.map { item ->
                if(item.tempId == id) {
                    item.copy(textElement = item.textElement.copy(attributes = item.textElement.attributes.newTextAttributes()))
                } else item
            }
            it.copy(tempTextList = newList)
        }
    }

    private fun updateTextAttribute(cardId: Long) {
        viewModelScope.launch {
            updateTextUseCase(
                UpdateTextParams(
                    cardId = cardId,
                    updates = _cardEditorState.value.tempTextList.map { it.textElement },
                    deletedIds = _deletedTextElementIds
                )
            ).onSuccess { result ->
                _cardEditorState.update {
                    val updatedList = (result.updatedElements + result.failedUpdates).map { text ->
                        TempTextElementState(textElement = text)
                    }
                    it.copy(
                        tempTextList = updatedList,
                        selectedTextTempId = updatedList.firstOrNull()?.tempId,
                    )
                }
                _deletedTextElementIds = result.deletedIds.toMutableSet()
            }
        }
    }

    private fun getCardDataFromDB(cardId: Long) {
        viewModelScope.launch {
            cardRepository.getCardById(cardId)
                .onSuccess { card ->
                    _cardEditorState.update { it.copy(selectedBackground = card.backgroundAssetId) }
                }
        }
    }

    private fun getObjectsFromDB() {
        viewModelScope.launch {
            assetRepository.getAssetsByType(AssetType.OBJECT)
                .onSuccess { objects ->
                    _cardEditorState.update { it.copy(objects = objects) }
                }
        }
    }

    private fun getBackgroundsFromDB() {
        viewModelScope.launch {
            assetRepository.getAssetsByType(AssetType.BACKGROUND)
                .onSuccess { backgrounds ->
                    _cardEditorState.update { it.copy(backgrounds = backgrounds) }
                }
        }
    }

    private fun loadMyObjectsFromDB(cardId: Long) {
        viewModelScope.launch {
            cardElementRepository.getObjectElementsWithAssetKeysByCardId(cardId)
                .collect { result ->
                    result.onSuccess {
                        _cardEditorState.update { state ->
                            state.copy(myObjects = it.reversed())
                        }
                    }
                }
        }
    }
}