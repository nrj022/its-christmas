package com.itschristmas.card.cardeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.card.cardeditor.model.DialogState
import com.itschristmas.card.cardeditor.model.PanelType
import com.itschristmas.card.cardeditor.model.TempTextElement
import com.itschristmas.card.cardeditor.model.TempTransform
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
        if(ime && state.panelType == PanelType.TEXT_EDITOR) 0.4f
        else state.unityContainerHeightFraction
    }

    fun onIntent(intent: CardEditorIntent) {
        when (intent) {
            is CardEditorIntent.Init -> handleInit(intent.cardId)
            is CardEditorIntent.ChangeTitle -> handleChangeTitle(intent.newTitle)
            is CardEditorIntent.FinishEditing -> handleFinishEditing()
            is CardEditorIntent.GenerateCard -> handleGenerateCard()
            is CardEditorIntent.ChangeDialogState -> handleChangeDialogState(intent.dialogState)

            is CardEditorIntent.CreateObject -> handleCreateObject(
                intent.cardId,
                intent.clickedObject
            )
            is CardEditorIntent.ChangeBackground -> handleChangeBackground(intent.assetId)
            is CardEditorIntent.SelectSpawnedObject -> handleSelectSpawnedObject(intent.element)
            is CardEditorIntent.DeleteSpawnedObject -> handleDeleteSpawnedObject()
            is CardEditorIntent.EnterTransformMode -> handleEnterTransformMode()
            is CardEditorIntent.EnterTextMode -> handleEnterTextMode(intent.cardId)

            is CardEditorIntent.ResetCamera -> handleResetCamera()

            /* 오브젝트 조정 */
            is CardEditorIntent.MoveObject -> handleMoveObject(intent.direction)
            is CardEditorIntent.ChangeScale -> handleChangeScale(intent.newScale)
            is CardEditorIntent.CancelTransform -> handleCancelTransform()
            is CardEditorIntent.ApplyTransform -> handleApplyTransform()
            is CardEditorIntent.ApplyAndExitTransform -> handleApplyAndExitTransform()
            is CardEditorIntent.DiscardAndExitTransform -> handleDiscardAndExitTransform()
            is CardEditorIntent.ResetTransform -> handleResetTransform()

            /* 텍스트 편집 */
            is CardEditorIntent.RequestDefaultText -> handleRequestDefaultText()
            is CardEditorIntent.AddText -> handleAddText()
            is CardEditorIntent.DeleteText -> handleDeleteText(intent.textId)
            is CardEditorIntent.SelectText -> handleSelectText(intent.textId)
            is CardEditorIntent.ChangeTextContent -> handleChangeTextContent(intent.newText)
            is CardEditorIntent.SelectAlignment -> handleSelectAlignment(intent.newAlignment)
            is CardEditorIntent.SelectColor -> handleSelectColor(intent.newColor)
            is CardEditorIntent.ChangeFontSize -> handleChangeFontSize(intent.newSize)
            is CardEditorIntent.SelectFont -> handleSelectFont(intent.newFont)
            is CardEditorIntent.MoveText -> handleMoveText(intent.direction)
            is CardEditorIntent.ApplyText -> handleApplyText(intent.cardId)
            is CardEditorIntent.ApplyAndExitText -> handleApplyAndExitText(intent.cardId)
            is CardEditorIntent.DiscardAndExitText -> handleDiscardAndExitText()
        }
    }

    private fun handleInit(cardId: Long) {
        getCardDataFromDB(cardId)
        getObjectsFromDB()
        getBackgroundsFromDB()

        // Spawned Objects Flow 구독
        loadSpawnedObjectsFromDB(cardId)
    }

    private fun handleChangeTitle(newTitle: String) {
        _cardEditorState.update { it.copy(cardTitle = newTitle) }
    }

    private fun handleFinishEditing() {
        updateDialogState(DialogState.SET_CARD_TITLE)
    }

    private fun handleGenerateCard() {
        /* TODO */
    }

    private fun handleChangeDialogState(dialogState: DialogState) {
        updateDialogState(dialogState)
    }


    private fun handleCreateObject(cardId: Long, clickedObject: Asset) {
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

    private fun handleChangeBackground(assetId: Long) {
        _cardEditorState.update {
            it.copy(selectedBackgroundId = if (it.selectedBackgroundId == assetId) null else assetId)
        }
    }

    private fun handleSelectSpawnedObject(element: CardElementWithAssetKeys) {
        _cardEditorState.update {
            it.copy(selectedSpawnedObject = if (it.selectedSpawnedObjectId == element.cardElement.elementId) null else element)
        }
    }

    private fun handleDeleteSpawnedObject() {
        viewModelScope.launch {
            _cardEditorState.value.selectedSpawnedObjectId?.let {
                cardElementRepository.deleteCardElementById(it)
            }
            updateDialogState(DialogState.NONE)
        }
    }

    private fun handleEnterTransformMode() {
        _cardEditorState.value.selectedSpawnedObject?.let { obj ->
            updatePanelType(PanelType.TRANSFORM_CONTROL)
            _cardEditorState.update {
                it.copy(
                    tempTransform = TempTransform(
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

    private fun handleEnterTextMode(cardId: Long) {
        updatePanelType(PanelType.TEXT_EDITOR)
        viewModelScope.launch {
            cardElementRepository.getTextElementsByCardId(cardId)
                .onSuccess { result ->
                    val newList = result.mapNotNull { element ->
                        element.textAttributes?.let { attr ->
                            TempTextElement(
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


    private fun handleResetCamera() {

    }

    /* 오브젝트 조정 */
    private fun handleMoveObject(direction: Direction) {
        val tempState = _cardEditorState.value.tempTransform ?: return
        _cardEditorState.update {
            it.copy(tempTransform =
                tempState.copy(
                    posY = tempState.posY + direction.dy,
                    posX = tempState.posX + direction.dx
                )
            )
        }
    }

    private fun handleChangeScale(newScale: Int) {
        if(newScale < 1) return

        _cardEditorState.update {
            it.copy(tempTransform = it.tempTransform?.copy(scale = newScale))
        }
    }

    private fun handleCancelTransform() {
        if(_cardEditorState.value.hasPendingTransform) {
            updateDialogState(DialogState.UNSAVED_TRANSFORM_CHANGES)
        } else {
            exitTransform()
        }
    }

    private fun handleApplyTransform() {
        updateTransform()
    }

    private fun handleApplyAndExitTransform() {
        updateTransform()
        exitTransform()
        updateDialogState(DialogState.NONE)
    }

    private fun handleDiscardAndExitTransform() {
        exitTransform()
        updateDialogState(DialogState.NONE)
    }

    private fun handleResetTransform() {
        val tempState = _cardEditorState.value.tempTransform ?: return
        val initialState = _cardEditorState.value.selectedSpawnedObject?.cardElement

        _cardEditorState.update {
            it.copy(tempTransform =
                tempState.copy(
                    posX = initialState?.posX ?: 0,
                    posY = initialState?.posY ?: 0,
                    scale = initialState?.scale ?: 1
                )
            )
        }
    }

    /* 텍스트 편집 */
    private fun handleRequestDefaultText() {
        _cardEditorState.update {
            if (it.tempTextList.isNotEmpty()) {
                if (it.selectedTextTempId == null) {
                    it.copy(selectedTextTempId = it.tempTextList.first().tempId)
                } else it
            } else {
                val newTextElement = TempTextElement()
                it.copy(
                    tempTextList = listOf(newTextElement),
                    selectedTextTempId = newTextElement.tempId
                )
            }
        }
    }

    private fun handleAddText() {
        val newTextElement = TempTextElement()
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

    private fun handleSelectText(textId: Long) {
        _cardEditorState.update {
            it.copy(
                selectedTextTempId = textId
            )
        }
    }

    private fun handleChangeTextContent(newText: String) {
        updateTempTextAttribute { copy(content = newText) }
    }

    private fun handleSelectAlignment(newAlignment: TextAlignmentOption) {
        updateTempTextAttribute { copy(alignment = newAlignment) }
    }

    private fun handleSelectColor(newColor: ColorOption) {
        updateTempTextAttribute { copy(textColor = newColor) }
    }

    private fun handleChangeFontSize(newSize: Float) {
        updateTempTextAttribute { copy(fontSize = newSize) }
    }

    private fun handleSelectFont(newFont: FontOption) {
        updateTempTextAttribute { copy(fontFamily = newFont) }
    }

    private fun handleMoveText(direction: Direction) {
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

    private fun handleApplyText(cardId: Long) {
        updateTextAttribute(cardId)
    }

    private fun handleApplyAndExitText(cardId: Long) {
        updateTextAttribute(cardId)
        updateDialogState(DialogState.NONE)
        updatePanelType(PanelType.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null) }
    }

    private fun handleDiscardAndExitText() {
        updateDialogState(DialogState.NONE)
        updatePanelType(PanelType.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null) }
    }


    private fun updatePanelType(panelType: PanelType) {
        _cardEditorState.update {
            it.copy(panelType = panelType)
        }
    }

    private fun updateDialogState(dialogState: DialogState) {
        _cardEditorState.update {
            it.copy(dialogState = dialogState)
        }
    }

    private fun updateTransform() {
        val tempState = _cardEditorState.value.tempTransform ?: return
        val initialState = _cardEditorState.value.selectedSpawnedObject ?: return

        viewModelScope.launch {
            cardElementRepository.updateElementTransform(
                elementId = tempState.elementId,
                posX = tempState.posX,
                posY = tempState.posY,
                scale = tempState.scale
            )
            _cardEditorState.update {
                it.copy(selectedSpawnedObject =
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
        updatePanelType(PanelType.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTransform = null) }
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
                        TempTextElement(textElement = text)
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
                    _cardEditorState.update { it.copy(selectedBackgroundId = card.backgroundAssetId) }
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

    private fun loadSpawnedObjectsFromDB(cardId: Long) {
        viewModelScope.launch {
            cardElementRepository.getObjectElementsWithAssetKeysByCardId(cardId)
                .collect { result ->
                    result.onSuccess {
                        _cardEditorState.update { state ->
                            state.copy(spawnedObjects = it.reversed())
                        }
                    }
                }
        }
    }
}