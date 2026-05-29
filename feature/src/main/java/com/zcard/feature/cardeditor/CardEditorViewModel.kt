package com.zcard.feature.cardeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.model.ElementType
import com.zcard.domain.model.Asset
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.bridge.UnityEventStatus
import com.zcard.domain.bridge.UnityEventType
import com.zcard.domain.bridge.UnityMessage
import com.zcard.domain.model.TextElement
import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetCardUseCase
import com.zcard.domain.usecase.UploadCardModelUseCase
import com.zcard.feature.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val generateCardUrlUseCase: GenerateCardUrlUseCase,
    private val uploadGlbUseCase: UploadCardModelUseCase,
) : ViewModel() {

    private var cardId: Long = -1L
    private var exportId: Long = -1L
    private var uploadJob: Job? = null

    private val _cardEditorState = MutableStateFlow(CardEditorState())
    val cardEditorState: StateFlow<CardEditorState> = _cardEditorState

    private val _cardEditorSideEffect = Channel<CardEditorSideEffect>(Channel.BUFFERED)
    val cardEditorSideEffect = _cardEditorSideEffect.receiveAsFlow()

    fun onIntent(intent: CardEditorIntent) {
        when (intent) {
            is CardEditorIntent.OnUnityMessage -> handleUnityMessage(intent.message)
            is CardEditorIntent.Init -> handleInit(intent.cardId)
            is CardEditorIntent.BackPressed -> handleLeaveAttempt()
            is CardEditorIntent.ChangeTitle -> handleChangeTitle(intent.newTitle)

            is CardEditorIntent.OpenCardLinkDetail -> handleOpenCardLinkDetail()
            is CardEditorIntent.SaveTitle -> handleSaveTitle()
            is CardEditorIntent.ResetTitle -> handleResetTitle()
            is CardEditorIntent.NavigateToCardShare -> handleNavigateToCardShare()

            is CardEditorIntent.FinishEditing -> handleFinishEditing()
            is CardEditorIntent.ExportGlbAndUpload -> handleExportGlbAndUpload()
            is CardEditorIntent.CancelUpload -> handleCancelUpload()
            is CardEditorIntent.CloseLoading -> handleLeaveAttempt()
            is CardEditorIntent.ChangeDialogState -> handleChangeDialogState(intent.dialogState)

            is CardEditorIntent.ChangeTab -> handleChangeTab(intent.tab)
            is CardEditorIntent.CreateObject -> handleCreateObject(intent.clickedObject)
            is CardEditorIntent.ChangeBackground -> handleChangeBackground(intent.assetId)
            is CardEditorIntent.SelectSpawnedObject -> handleSelectSpawnedObject(intent.element)
            is CardEditorIntent.DeleteSpawnedObject -> handleDeleteSpawnedObject()
            is CardEditorIntent.EnterTransformMode -> handleEnterTransformMode()
            is CardEditorIntent.EnterTextMode -> handleEnterTextMode()

            is CardEditorIntent.ResetCamera -> handleResetCamera()
        }
    }

    // ── 초기 설정 ─────────────────────────────────────────────────────────────

    private fun handleInit(cardId: Long) {
        viewModelScope.launch {
            getCardUseCase(cardId)
                .onSuccess { result ->
                    this@CardEditorViewModel.cardId = result.cardData.cardId
                    exportId = result.cardData.exportId
                    _cardEditorState.update {
                        it.copy(
                            originalCardTitle = result.cardData.title,
                            cardTitle = result.cardData.title,
                            cardUrl = result.cardUrl,
                            selectedBackgroundId = result.cardData.backgroundAssetId,
                            objects = result.objects,
                            backgrounds = result.backgrounds,
                            spawnedObjects = result.spawnedObjects,
                            loadingObjectIds = result.spawnedObjects.map { obj -> obj.cardElement.elementId }.toSet()
                        )
                    }
                    observeSpawnedObjects(result.spawnedObjectsFlow)
                    initUnity(result.spawnedObjects, result.texts)
                }.onFailure {
                    Log.e(TAG, "handleInit: Load Card Failed\n$it")
                    delay(1000L)    // UX 개선 및 SideEffect 놓침 방지
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_load_card))
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.Finish)
                }
        }
    }

    private fun initUnity(spawnedObjects: List<CardElementWithAssetKeys>, texts: List<TextElement>) {
        val bgAssetId = _cardEditorState.value.selectedBackgroundId

        unityBridge.initScene(spawnedObjects, texts)
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

    private fun handleLeaveAttempt() {
        if(uploadJob?.isActive == true) {
            updateDialogState(CardEditorState.DialogState.UPLOAD_CANCEL_CONFIRM)
        } else if(_cardEditorState.value.isLoading) {
            _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_block_back_during_export))
        } else {
            _cardEditorSideEffect.trySend(CardEditorSideEffect.Finish)
        }
    }

    // ── Unity 메시지 ──────────────────────────────────────────────────────────

    private fun handleUnityMessage(msg: UnityMessage) {
        when (msg.type) {
            UnityEventType.CREATE_OBJECT -> handleCreateObjectResult(msg.status, msg.data)
            UnityEventType.EXPORT_GLB -> handleUploadGlbResult(msg.status, msg.data)
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
                cardElementRepository.deleteCardElementById(cardId, id)
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

    private fun handleUploadGlbResult(unityStatusType: UnityEventStatus, fileName: String) {
        uploadJob = viewModelScope.launch {
            try {
                if(unityStatusType != UnityEventStatus.SUCCESS) error(fileName)

                uploadGlbUseCase(cardId, fileName).collect { state ->
                    when(state) {
                        is UploadState.Progress -> _cardEditorState.update {
                            it.copy(loadingText = "Uploading ${state.percent}%")
                        }
                        is UploadState.Success -> {
                            val cardUrl = generateCardUrlUseCase(cardId).getOrThrow()
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
                uploadJob = null
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
        updateDialogState(CardEditorState.DialogState.CARD_LINK_DETAIL)
    }

    private fun handleNavigateToCardShare() {
        viewModelScope.launch {
            val cardUrl = generateCardUrlUseCase(cardId).getOrThrow()
            _cardEditorState.update { it.copy(dialogState = CardEditorState.DialogState.NONE) }
            _cardEditorSideEffect.trySend(CardEditorSideEffect.NavigateToCardShare(cardUrl))
        }
    }

    private fun handleFinishEditing() {
        updateDialogState(CardEditorState.DialogState.SET_CARD_TITLE)
    }

    private fun handleExportGlbAndUpload() {
        saveCardTitle()
        updateDialogState(CardEditorState.DialogState.NONE)

        if(exportId == -1L) return
        unityBridge.exportGlb(exportId)

        _cardEditorState.update {
            it.copy(
                isLoading = true,
                loadingText = "Exporting"
            )
        }
    }

    private fun handleCancelUpload() {
        updateDialogState(CardEditorState.DialogState.NONE)
        val job = uploadJob ?: return
        job.cancel()
        _cardEditorState.update { it.copy(isLoading = false) }
        _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_cancel_card_upload))
    }

    private fun handleChangeDialogState(dialogState: CardEditorState.DialogState) {
        updateDialogState(dialogState)
    }

    // ── 오브젝트 ──────────────────────────────────────────────────────────────

    private fun handleChangeTab(tab: CardEditorState.AssetBrowserTab) {
        _cardEditorState.update { it.copy(selectedTab = tab) }
    }

    private fun handleCreateObject(clickedObject: Asset) {
        viewModelScope.launch {
            val newElement = CardElement(
                elementId = 0,
                cardId = cardId,
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
            cardRepository.updateBackgroundAssetId(cardId, assetId)
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
                cardElementRepository.deleteCardElementById(cardId, id)
                    .onSuccess { row ->
                        if(row > 0) unityBridge.deleteObject(id)
                    }
            }
            updateDialogState(CardEditorState.DialogState.NONE)
            _cardEditorState.update { it.copy(selectedSpawnedObject = null) }
        }
    }

    private fun handleEnterTransformMode() {
        val selectedObject = _cardEditorState.value.selectedSpawnedObject ?: return
        _cardEditorSideEffect.trySend(CardEditorSideEffect.NavigateToTransform(selectedObject.cardElement.elementId))
    }

    private fun handleEnterTextMode() {
        _cardEditorSideEffect.trySend(CardEditorSideEffect.NavigateToTextEdit)
    }

    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    // ── 공통 유틸 ─────────────────────────────────────────────────────────────

    private fun updateDialogState(dialogState: CardEditorState.DialogState) {
        _cardEditorState.update { it.copy(dialogState = dialogState) }
    }

    private fun saveCardTitle() {
        val title = _cardEditorState.value.cardTitle
        viewModelScope.launch {
            cardRepository.updateCardTitle(cardId, title)
                .onSuccess {
                    _cardEditorState.update { it.copy(originalCardTitle = title) }
                }.onFailure {
                    Log.e(TAG, "handleSaveTitle: $it")
                    _cardEditorSideEffect.trySend(CardEditorSideEffect.ToastMessage(R.string.editor_msg_fail_card_title_update))
                }
        }
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