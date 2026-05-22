package com.zcard.feature.cardeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.model.PanelType
import com.zcard.feature.cardeditor.model.TempTextElement
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.ElementType
import com.zcard.domain.model.Asset
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextFontFamily
import com.zcard.domain.model.TextAlignment
import com.zcard.domain.model.TextAttributes
import com.zcard.domain.model.TextElement
import com.zcard.domain.bridge.UnityEventStatus
import com.zcard.domain.bridge.UnityEventType
import com.zcard.domain.bridge.UnityMessage
import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetCardUseCase
import com.zcard.domain.usecase.GetTextElementsUseCase
import com.zcard.domain.usecase.SaveTextElementsParams
import com.zcard.domain.usecase.SaveTextElementsUseCase
import com.zcard.domain.usecase.UploadCardModelUseCase
import com.zcard.feature.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long
import kotlin.onSuccess

private const val TAG = "CardEditorViewModel"

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val unityBridge: UnityBridge,
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository,
    private val cardElementRepository: CardElementRepository,
    private val getCardUseCase: GetCardUseCase,
    private val getTextsUseCase: GetTextElementsUseCase,
    private val saveTextElementsUseCase: SaveTextElementsUseCase,
    private val generateCardUrlUseCase: GenerateCardUrlUseCase,
    private val uploadGlbUseCase: UploadCardModelUseCase,
) : ViewModel() {

    private var _cardId: Long = -1L
    private var _exportId: Long = -1L

    private var _deletedTextElementIds: MutableSet<Long> = mutableSetOf()

    private val _cardEditorState = MutableStateFlow(CardEditorState())
    val cardEditorState: StateFlow<CardEditorState> = _cardEditorState

    private val _cardEditorSideEffect = Channel<CardEditorSideEffect>(Channel.BUFFERED)
    val cardEditorSideEffect = _cardEditorSideEffect.receiveAsFlow()

    private val _imeVisible = MutableStateFlow(false)
    private val imeVisible = _imeVisible.asStateFlow()

    fun setImeVisible(visible: Boolean) {
        _imeVisible.value = visible
    }

    val unityContainerHeightFractionFlow = combine(cardEditorState, imeVisible) { state, ime ->
        if(ime && state.panelType == PanelType.TEXT_EDITOR) 0.4f
        else state.unityContainerHeightFraction
    }

    fun onIntent(intent: CardEditorIntent) {
        when (intent) {
            is CardEditorIntent.OnUnityMessage -> handleUnityMessage(intent.message)
            is CardEditorIntent.Init -> handleInit(intent.cardId)
            is CardEditorIntent.ChangeTitle -> handleChangeTitle(intent.newTitle)

            is CardEditorIntent.OpenCardLinkDetail -> handleOpenCardLinkDetail()
            is CardEditorIntent.SaveTitle -> handleSaveTitle()
            is CardEditorIntent.ResetTitle -> handleResetTitle()
            is CardEditorIntent.CopyCardLink -> handleCopyCardLink()

            is CardEditorIntent.FinishEditing -> handleFinishEditing()
            is CardEditorIntent.ExportGlbAndUpload -> handleExportGlbAndUpload()
            is CardEditorIntent.ChangeDialogState -> handleChangeDialogState(intent.dialogState)

            is CardEditorIntent.CreateObject -> handleCreateObject(intent.clickedObject)
            is CardEditorIntent.ChangeBackground -> handleChangeBackground(intent.assetId)
            is CardEditorIntent.SelectSpawnedObject -> handleSelectSpawnedObject(intent.element)
            is CardEditorIntent.DeleteSpawnedObject -> handleDeleteSpawnedObject()
            is CardEditorIntent.EnterTransformMode -> handleEnterTransformMode()
            is CardEditorIntent.EnterTextMode -> handleEnterTextMode()

            is CardEditorIntent.ResetCamera -> handleResetCamera()

            /* 텍스트 편집 패널 */
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

    // ── 씬 초기화 ─────────────────────────────────────────────────────────────

    private fun handleInit(cardId: Long) {
        viewModelScope.launch {
            getCardUseCase(cardId)
                .onSuccess { result ->
                    _cardId = result.cardData.cardId
                    _exportId = result.cardData.exportId
                    _cardEditorState.update {
                        it.copy(
                            originalCardTitle = result.cardData.title,
                            cardTitle = result.cardData.title,
                            cardUrl = result.cardUrl,
                            selectedBackgroundId = result.cardData.backgroundAssetId,
                            objects = result.objects,
                            texts = result.texts,
                            backgrounds = result.backgrounds,
                            spawnedObjects = result.spawnedObjects,
                            loadingObjectIds = result.spawnedObjects.map { obj -> obj.cardElement.elementId }.toSet()
                        )
                    }
                    observeSpawnedObjects(result.spawnedObjectsFlow)
                    initUnity()
                }.onFailure {
                    Log.e(TAG, "handleInit: Load Card Failed\n$it")
                    delay(1000L)    // UX 개선 및 SideEffect 놓침 방지
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_load_card))
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.Finish)
                }
        }
    }

    private fun initUnity() {
        val bgAssetId = _cardEditorState.value.selectedBackgroundId

        unityBridge.initScene(
            _cardEditorState.value.spawnedObjects,
            _cardEditorState.value.texts
        )
        unityChangeBackground(bgAssetId)
    }

    private fun observeSpawnedObjects(flow: Flow<Result<List<CardElementWithAssetKeys>>>) {
        // Room Flow는 cold flow 이므로 collect를 시작할 때마다 새로 데이터를 읽어서 emit
        flow.onEach { result ->
            result.onSuccess { objects ->
                _cardEditorState.update { it.copy(spawnedObjects = objects) }
            }
            result.exceptionOrNull()?.let { error ->
                Log.e(TAG, "observeSpawnedObjects: $error")
                _cardEditorSideEffect.trySend(
                    CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_refresh_objects)
                )
            }
        }.launchIn(viewModelScope)
    }

    // ── Unity 메시지 ──────────────────────────────────────────────────────────

    private fun handleUnityMessage(msg: UnityMessage) {
        when (msg.type) {
            UnityEventType.CREATE_OBJECT -> handleCreateObjectResult(msg.status, msg.data)
            UnityEventType.EXPORT_GLB -> handleExportGlbResult(msg.status, msg.data)
            else -> Unit
        }
    }

    private fun handleCreateObjectResult(unityStatusType: UnityEventStatus, result: String) {
        viewModelScope.launch {
            val id = result.toLongOrNull() ?: return@launch
            val selectedObject: CardElementWithAssetKeys?

            // 오브젝트 생성 실패 시 cardElement 에서 지우고 에러 메세지 Toast
            if(unityStatusType == UnityEventStatus.FAILURE) {
                Log.e(TAG, "Id $result Object Creation Failed")
                selectedObject = null
                cardElementRepository.deleteCardElementById(_cardId, id)
                    .onFailure { Log.e(TAG, "Zombie data created. Element ID: $id") }
                _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_load_object))
            } else {
                selectedObject = _cardEditorState.value.spawnedObjects.find { obj -> obj.cardElement.elementId == id }
                unityBridge.selectObject(id)
            }

            _cardEditorState.update {
                it.copy(
                    loadingObjectIds = it.loadingObjectIds - id,
                    selectedSpawnedObject = selectedObject
                )
            }
        }
    }

    private fun handleExportGlbResult(unityStatusType: UnityEventStatus, fileName: String) {
        viewModelScope.launch {
            try {
                if(unityStatusType != UnityEventStatus.SUCCESS) error(fileName)

                uploadGlbUseCase(_cardId, fileName).collect { state ->
                    when(state) {
                        is UploadState.Progress -> _cardEditorState.update {
                            it.copy(loadingText = "Uploading ${state.percent}%")
                        }
                        is UploadState.Success -> {
                            val cardUrl = generateCardUrlUseCase(_cardId).getOrThrow()
                            _cardEditorSideEffect.trySend(CardEditorSideEffect.NavigateToCardShare(cardUrl))
                        }
                        is UploadState.Failure -> throw state.error ?: Exception("Firebase upload failed")
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Log.e(TAG, "handleExportGlbResult: $e")
                _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_card_upload))
            } finally {
                _cardEditorState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ── 카드 정보 ─────────────────────────────────────────────────────────────

    private fun handleChangeTitle(newTitle: String) {
        _cardEditorState.update { it.copy(cardTitle = newTitle) }
    }

    private fun handleSaveTitle() {
        saveCardTitle()
    }

    private fun handleResetTitle() {
        _cardEditorState.update { it.copy(cardTitle = it.originalCardTitle) }
    }

    private fun handleOpenCardLinkDetail() {
        updateDialogState(DialogState.CARD_LINK_DETAIL)
    }

    private fun handleCopyCardLink() {
        viewModelScope.launch {
            _cardEditorSideEffect.trySend(CardEditorSideEffect.CopyCardLink(_cardEditorState.value.cardUrl))
        }
    }

    private fun handleFinishEditing() {
        updateDialogState(DialogState.SET_CARD_TITLE)
    }

    private fun handleExportGlbAndUpload() {
        saveCardTitle()
        updateDialogState(DialogState.NONE)

        if(_exportId == -1L) return
        unityBridge.exportGlb(_exportId)

        _cardEditorState.update {
            it.copy(
                isLoading = true,
                loadingText = "Exporting"
            )
        }
    }

    private fun handleChangeDialogState(dialogState: DialogState) {
        updateDialogState(dialogState)
    }

    // ── 오브젝트 ──────────────────────────────────────────────────────────────

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
                    _cardEditorState.update { it.copy(loadingObjectIds = it.loadingObjectIds + id) }
                    unityBridge.createObject(
                        unityKey = clickedObject.unityKey,
                        elementId = id,
                        element = newElement
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
            _cardEditorState.update { it.copy(selectedSpawnedObject = null) }
        }
    }

    private fun handleEnterTransformMode() {
        val selectedObject = _cardEditorState.value.selectedSpawnedObject ?: return
        _cardEditorSideEffect.trySend(CardEditorSideEffect.NavigateToTransform(selectedObject.cardElement.elementId))
    }

    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    // ── 텍스트 편집 패널 ──────────────────────────────────────────────────────

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

    private fun handleSelectAlignment(newAlignment: TextAlignment) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(alignment = newAlignment) }
        unityBridge.updateTextAlign(textId, newAlignment.alignCode)
    }

    private fun handleSelectColor(newColor: TextColor) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(textColor = newColor) }
        unityBridge.updateTextColor(textId, newColor.rgbaColor)
    }

    private fun handleChangeFontSize(newSize: Float) {
        val textId = _cardEditorState.value.selectedTextTempId ?: return
        updateTempTextAttribute(textId) { copy(fontSize = newSize) }
        unityBridge.updateFontSize(textId, newSize)
    }

    private fun handleSelectFont(newFont: TextFontFamily) {
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

    // ── 공통 유틸 ─────────────────────────────────────────────────────────────

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
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_card_title_update))
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
}