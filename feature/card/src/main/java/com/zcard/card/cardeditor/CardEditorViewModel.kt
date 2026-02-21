package com.zcard.card.cardeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.card.cardeditor.model.Direction
import com.zcard.card.cardeditor.model.DialogState
import com.zcard.card.cardeditor.model.PanelType
import com.zcard.card.cardeditor.model.TempTextElement
import com.zcard.card.cardeditor.model.TempTransform
import com.zcard.card.cardeditor.util.extractFileNameAndToken
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.enum.ElementType
import com.zcard.domain.model.Asset
import com.zcard.domain.model.Card
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.ColorOption
import com.zcard.domain.model.FontOption
import com.zcard.domain.model.TextAlignmentOption
import com.zcard.domain.model.TextAttributes
import com.zcard.domain.model.TextElement
import com.zcard.domain.model.UnityStatusType
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetTextElementsUseCase
import com.zcard.domain.usecase.LoadCardEditorUseCase
import com.zcard.domain.usecase.SaveTextElementsParams
import com.zcard.domain.usecase.SaveTextElementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long
import kotlin.onSuccess

private const val TAG = "CardEditorViewModel"

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository,
    private val cardElementRepository: CardElementRepository,
    private val loadCardEditorUseCase: LoadCardEditorUseCase,
    private val getTextsUseCase: GetTextElementsUseCase,
    private val saveTextElementsUseCase: SaveTextElementsUseCase,
    private val generateCardUrl: GenerateCardUrlUseCase,
    private val unityBridge: UnityBridge
) : ViewModel() {

    private var _cardId: Long = -1L

    private val _cardEditorState = MutableStateFlow(CardEditorState())
    val cardEditorState: StateFlow<CardEditorState> = _cardEditorState

    private val _cardEditorSideEffect = MutableSharedFlow<CardEditorSideEffect>()
    val cardEditorSideEffect: SharedFlow<CardEditorSideEffect> = _cardEditorSideEffect


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

            is CardEditorIntent.OpenCardLinkDetail -> handleOpenCardLinkDetail()
            is CardEditorIntent.SaveTitle -> handleSaveTitle()
            is CardEditorIntent.ResetTitle -> handleResetTitle()
            is CardEditorIntent.CopyCardLink -> handleCopyCardLink()

            is CardEditorIntent.FinishEditing -> handleFinishEditing()
            is CardEditorIntent.ExportGlbAndUpload -> handleExportGlbAndUpload()
            is CardEditorIntent.ExportGlbResult -> handleExportGlbResult(intent.unityStatusType, intent.result)
            is CardEditorIntent.ChangeDialogState -> handleChangeDialogState(intent.dialogState)

            is CardEditorIntent.CreateObject -> handleCreateObject(intent.clickedObject)
            is CardEditorIntent.ChangeBackground -> handleChangeBackground(intent.assetId)
            is CardEditorIntent.SelectSpawnedObject -> handleSelectSpawnedObject(intent.element)
            is CardEditorIntent.DeleteSpawnedObject -> handleDeleteSpawnedObject()
            is CardEditorIntent.EnterTransformMode -> handleEnterTransformMode()
            is CardEditorIntent.EnterTextMode -> handleEnterTextMode()

            is CardEditorIntent.ResetCamera -> handleResetCamera()

            /* 오브젝트 조정 */
            is CardEditorIntent.MoveObject -> handleMoveObject(intent.direction)
            is CardEditorIntent.ChangeScale -> handleChangeScale(intent.newScale)
            is CardEditorIntent.CancelTransform -> handleCancelTransform()
            is CardEditorIntent.ApplyTransform -> handleApplyTransform()
            is CardEditorIntent.ApplyAndExitTransform -> handleApplyAndExitTransform()
            is CardEditorIntent.DiscardAndExitTransform -> handleDiscardAndExitTransform()
            is CardEditorIntent.ResetTransform -> handleResetTransform()
            is CardEditorIntent.CameraFocus -> handleCameraFocus()

            /* 텍스트 편집 */
            is CardEditorIntent.MissingTextSelection -> handleMissingTextSelection()
            is CardEditorIntent.AddText -> handleAddText()
            is CardEditorIntent.DeleteText -> handleDeleteText(intent.textId)
            is CardEditorIntent.SelectText -> handleSelectText(intent.textId)
            is CardEditorIntent.ChangeTextContent -> handleChangeTextContent(intent.newText)
            is CardEditorIntent.SelectAlignment -> handleSelectAlignment(intent.newAlignment)
            is CardEditorIntent.SelectColor -> handleSelectColor(intent.newColor)
            is CardEditorIntent.ChangeFontSize -> handleChangeFontSize(intent.newSize)
            is CardEditorIntent.SelectFont -> handleSelectFont(intent.newFont)
            is CardEditorIntent.MoveText -> handleMoveText(intent.direction)
            is CardEditorIntent.ApplyText -> handleApplyText()
            is CardEditorIntent.ApplyAndExitText -> handleApplyAndExitText()
            is CardEditorIntent.DiscardAndExitText -> handleDiscardAndExitText()
        }
    }

    private fun handleInit(cardId: Long) {
        _cardId = cardId
        viewModelScope.launch {
            if(_cardId < 0) {
                cardRepository.insertCard(Card())
                    .onSuccess { id -> _cardId = id}
                    .onFailure {
                        /* TODO */
                    }
            }
            loadCardEditorUseCase(_cardId)
                .onSuccess { result ->
                    _cardEditorState.update {
                        it.copy(
                            originalCardTitle = result.cardData.title,
                            cardTitle = result.cardData.title,
                            cardUrl = result.cardUrl,
                            selectedBackgroundId = result.cardData.backgroundAssetId,
                            objects = result.objects,
                            texts = result.texts,
                            backgrounds = result.backgrounds
                        )
                    }
                    launch {
                        result.spawnedObjectsFlow.collect {
                            it.onSuccess { objects ->
                                _cardEditorState.update { state ->
                                    state.copy(spawnedObjects = objects.asReversed())
                                }
                            }
                        }
                    }
                }.onFailure {
                    /* TODO */
                }
        }
    }

    fun handleInitUnity() {
        val bgAssetId = _cardEditorState.value.selectedBackgroundId

        unityBridge.initScene(
            _cardEditorState.value.spawnedObjects,
            _cardEditorState.value.texts
        )
        unityChangeBackground(bgAssetId)

        _cardEditorState.update { it.copy(isLoading = false) }
    }

    private fun handleChangeTitle(newTitle: String) {
        _cardEditorState.update { it.copy(cardTitle = newTitle) }
    }

    private fun handleOpenCardLinkDetail() {
        updateDialogState(DialogState.CARD_LINK_DETAIL)
    }

    private fun handleSaveTitle() {
        saveCardTitle()
    }

    private fun handleResetTitle() {
        _cardEditorState.update { it.copy(cardTitle = it.originalCardTitle) }
    }

    private fun handleCopyCardLink() {
        viewModelScope.launch {
            _cardEditorSideEffect.emit(CardEditorSideEffect.CopyCardLink(_cardEditorState.value.cardUrl))
        }
    }

    private fun handleFinishEditing() {
        updateDialogState(DialogState.SET_CARD_TITLE)
    }

    private fun handleExportGlbAndUpload() {
        saveCardTitle()
        updateDialogState(DialogState.NONE)
        _cardEditorState.update { it.copy(isLoading = true) }
        unityBridge.exportAndUpload()
    }

    private fun handleExportGlbResult(unityStatusType: UnityStatusType, result: String) {
        viewModelScope.launch {
            try {
                if(unityStatusType != UnityStatusType.SUCCESS) error("Export glb failed from Unity")

                val (fileName, token) = extractFileNameAndToken(result)
                cardRepository.updateGlb(_cardId, fileName, token).getOrThrow()

                val cardUrl = generateCardUrl(_cardId)
                if(cardUrl.isBlank()) error("Generated card url is blank")

                _cardEditorSideEffect.emit(CardEditorSideEffect.NavigateToCardShare(cardUrl))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Log.e(TAG, "handleExportGlbResult: $e")
                _cardEditorSideEffect.emit(CardEditorSideEffect.ShowToast("Oops! Card generation failed. Please try again."))
            } finally {
                _cardEditorState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleChangeDialogState(dialogState: DialogState) {
        updateDialogState(dialogState)
    }


    private fun handleCreateObject(clickedObject: Asset) {
        viewModelScope.launch {
            val newElement = CardElement(
                elementId = 0,
                cardId = _cardId,
                assetId = clickedObject.assetId,
                elementType = ElementType.OBJECT,
            )
            cardElementRepository.insertCardElement(newElement)
                .onSuccess { id ->
                    unityBridge.createObject(
                        unityKey = clickedObject.unityKey,
                        elementId = id,
                        posX = newElement.posX,
                        posY = newElement.posY,
                        posZ = newElement.posZ,
                        scale = newElement.scale
                    )
            }
        }
    }

    private fun handleChangeBackground(assetId: Long) {
        val currentBackgroundId = _cardEditorState.value.selectedBackgroundId
        if (currentBackgroundId == assetId) return

        viewModelScope.launch {
            cardRepository.updateBackgroundAssetId(_cardId, assetId)
        }
        _cardEditorState.update {
            it.copy(selectedBackgroundId = assetId)
        }
        unityChangeBackground(assetId)
    }

    private fun handleSelectSpawnedObject(element: CardElementWithAssetKeys) {
        val alreadySelected = _cardEditorState.value.selectedSpawnedObjectId == element.cardElement.elementId

        _cardEditorState.update {
            it.copy(selectedSpawnedObject = if (alreadySelected) null else element)
        }

        if (alreadySelected) {
            unityBridge.clearSelection()
        } else {
            unityBridge.selectObject(element.cardElement.elementId)
        }
    }

    private fun handleDeleteSpawnedObject() {
        viewModelScope.launch {
            _cardEditorState.value.selectedSpawnedObjectId?.let { id ->
                cardElementRepository.deleteCardElementById(_cardId, id)
                    .onSuccess { row ->
                        if(row > 0) unityBridge.deleteObject(id)
                    }
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
                        posZ = obj.cardElement.posZ,
                        scale = obj.cardElement.scale
                    ),
                    isTransformCameraFocus = true
                )
            }
        }
    }

    private fun handleEnterTextMode() {
        val texts = _cardEditorState.value.texts
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
        _cardEditorState.update {
            it.copy(
                tempTextList = newList,
                selectedTextTempId = newList.firstOrNull()?.tempId
            )
        }
        updatePanelType(PanelType.TEXT_EDITOR)

        unityBridge.clearAllTexts()
        newList.forEach {
            unityBridge.createText(
                tempId = it.tempId,
                textElement = it.textElement
            )
        }
    }


    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    /* 오브젝트 조정 */
    private fun handleMoveObject(direction: Direction) {
        val tempState = _cardEditorState.value.tempTransform ?: return
        val updatedPosX = tempState.posX + direction.dx
        val updatedPosY = tempState.posY + direction.dy
        val updatedPosZ = tempState.posZ + direction.dz

        _cardEditorState.update {
            it.copy(tempTransform =
                tempState.copy(posX = updatedPosX, posY = updatedPosY, posZ = updatedPosZ)
            )
        }

        unityBridge.updatePosition(tempState.elementId, updatedPosX, updatedPosY, updatedPosZ)
    }

    private fun handleChangeScale(newScale: Int) {
        val tempState = _cardEditorState.value.tempTransform ?: return
        if(newScale < 1) return

        _cardEditorState.update {
            it.copy(tempTransform = tempState.copy(scale = newScale))
        }

        unityBridge.updateScale(tempState.elementId, newScale)
    }

    private fun handleCancelTransform() {
        if(_cardEditorState.value.hasPendingTransform) {
            updateDialogState(DialogState.UNSAVED_TRANSFORM_CHANGES)
        } else {
            exitTransform()
        }
    }

    private fun handleApplyTransform() {
        saveTransformChanges()
    }

    private fun handleApplyAndExitTransform() {
        saveTransformChanges()
        exitTransform()
        updateDialogState(DialogState.NONE)
    }

    private fun handleDiscardAndExitTransform() {
        exitTransform()
        unityResetTransform()
        updateDialogState(DialogState.NONE)
    }

    private fun handleResetTransform() {
        val tempState = _cardEditorState.value.tempTransform ?: return
        val initialState = _cardEditorState.value.selectedSpawnedObject?.cardElement

        _cardEditorState.update {
            it.copy(tempTransform =
                tempState.copy(
                    posX = initialState?.posX ?: 0f,
                    posY = initialState?.posY ?: 0f,
                    posZ = initialState?.posZ ?: 0f,
                    scale = initialState?.scale ?: 1
                )
            )
        }

        unityResetTransform()
    }

    private fun handleCameraFocus() {
        val cameraFocus = _cardEditorState.value.isTransformCameraFocus

        if(cameraFocus) {
            unityBridge.clearSelection()
        } else {
            val elementId = _cardEditorState.value.selectedSpawnedObject?.cardElement?.elementId ?: return
            unityBridge.selectObject(elementId)
        }

        _cardEditorState.update { it.copy(isTransformCameraFocus = !cameraFocus) }
    }

    /* 텍스트 편집 */
    private fun handleMissingTextSelection() {
        if (_cardEditorState.value.tempTextList.isNotEmpty() && _cardEditorState.value.selectedTextTempId == null) {
            _cardEditorState.update { it.copy(selectedTextTempId = it.tempTextList.first().tempId) }
            return
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

        unityBridge.createText(
            tempId = newTextElement.tempId,
            textElement = newTextElement.textElement
        )
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

        unityBridge.deleteObject(selectedText.tempId)
    }

    private fun handleSelectText(textId: Long) {
        _cardEditorState.update { it.copy(selectedTextTempId = textId) }
        unityBridge.selectObject(textId)
    }

    private fun handleChangeTextContent(newText: String) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(content = newText) }
        unityBridge.updateTextContent(_cardEditorState.value.selectedTextTempId ?: 0, newText)
    }

    private fun handleSelectAlignment(newAlignment: TextAlignmentOption) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(alignment = newAlignment) }
        unityBridge.updateTextAlign(textId, newAlignment.alignCode)
    }

    private fun handleSelectColor(newColor: ColorOption) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(textColor = newColor) }
        unityBridge.updateTextColor(textId, newColor.rgbaColor)
    }

    private fun handleChangeFontSize(newSize: Float) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(fontSize = newSize) }
        unityBridge.updateFontSize(textId, newSize)
    }

    private fun handleSelectFont(newFont: FontOption) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(fontFamily = newFont) }
        unityBridge.updateFont(textId, newFont.key)
    }

    private fun handleMoveText(direction: Direction) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        _cardEditorState.update {
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

    private fun handleApplyText() {
        saveTextChanges()
    }

    private fun handleApplyAndExitText() {
        saveTextChanges(true)
        exitTextEditor()
    }

    private fun handleDiscardAndExitText() {
        viewModelScope.launch {
            getTextsUseCase(_cardId)
                .onSuccess { result ->
                    _cardEditorState.update { it.copy(texts = result) }
                    unityBridge.replaceAllTexts(result)
                }
        }
        exitTextEditor()
    }


    /* 공통 함수 */
    private fun updatePanelType(panelType: PanelType) {
        _cardEditorState.update { it.copy(panelType = panelType) }
    }

    private fun updateDialogState(dialogState: DialogState) {
        _cardEditorState.update { it.copy(dialogState = dialogState) }
    }

    private fun updateTempTextAttribute(textId: Long, newTextAttributes: TextAttributes.() -> TextAttributes) {
        _cardEditorState.update {
            val newList = it.tempTextList.map { item ->
                if(item.tempId == textId) {
                    item.copy(textElement = item.textElement.copy(attributes = item.textElement.attributes.newTextAttributes()))
                } else item
            }
            it.copy(tempTextList = newList)
        }
    }

    private fun saveCardTitle() {
        val title = _cardEditorState.value.cardTitle
        viewModelScope.launch {
            cardRepository.updateCardTitle(_cardId, title)
                .onSuccess {
                    _cardEditorState.update { it.copy(originalCardTitle = title) }
                }.onFailure {
                    Log.e(TAG, "handleSaveTitle: $it")
                    _cardEditorSideEffect.emit(CardEditorSideEffect.ShowToast("Oops! Card title update failed. Please try again."))
                }
        }
    }

    private fun saveTransformChanges() {
        val tempState = _cardEditorState.value.tempTransform ?: return
        val initialState = _cardEditorState.value.selectedSpawnedObject ?: return

        viewModelScope.launch {
            cardElementRepository.updateElementTransform(
                cardId = _cardId,
                elementId = tempState.elementId,
                posX = tempState.posX,
                posY = tempState.posY,
                posZ = tempState.posZ,
                scale = tempState.scale
            ).onSuccess {
                _cardEditorState.update {
                    it.copy(selectedSpawnedObject =
                        initialState.copy(
                            cardElement = initialState.cardElement.copy(
                                posX = tempState.posX,
                                posY = tempState.posY,
                                posZ = tempState.posZ,
                                scale = tempState.scale
                            )
                        )
                    )
                }
            }
        }
    }

    private fun saveTextChanges(shouldFinishEditing: Boolean = false) {
        viewModelScope.launch {
            saveTextElementsUseCase(
                SaveTextElementsParams(
                    cardId = _cardId,
                    updates = _cardEditorState.value.tempTextList.map { it.textElement },
                    deletedIds = _deletedTextElementIds
                )
            ).onSuccess { result ->
                if(!shouldFinishEditing) {
                    _cardEditorState.update {
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
                    _cardEditorState.update {
                        it.copy(texts = result.updatedElements)
                    }
                    unityBridge.replaceAllTexts(result.updatedElements)
                }
            }
        }
    }

    private fun exitTransform() {
        updatePanelType(PanelType.ASSET_BROWSER)
        _cardEditorState.update { it.copy(tempTransform = null) }
    }

    private fun exitTextEditor() {
        updateDialogState(DialogState.NONE)
        updatePanelType(PanelType.ASSET_BROWSER)

        _cardEditorState.update { it.copy(tempTextList = emptyList(), selectedTextTempId = null) }
        _deletedTextElementIds = mutableSetOf()
    }

    private fun unityChangeBackground(assetId: Long) {
        viewModelScope.launch {
            assetRepository.getAssetById(assetId)
                .onSuccess { asset ->
                    unityBridge.changeBackground(asset.unityKey)
                }
        }
    }

    private fun unityResetTransform() {
        val initialState = _cardEditorState.value.selectedSpawnedObject?.cardElement ?: return

        val initialPosX = initialState.posX
        val initialPosY = initialState.posY
        val initialPosZ = initialState.posZ
        val initialScale = initialState.scale

        unityBridge.updatePosition(initialState.elementId, initialPosX, initialPosY, initialPosZ)
        unityBridge.updateScale(initialState.elementId, initialScale)
    }
}