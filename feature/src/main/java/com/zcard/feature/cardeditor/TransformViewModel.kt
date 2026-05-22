package com.zcard.feature.cardeditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.bridge.UnityBridge
import com.zcard.domain.repository.CardElementRepository
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.feature.cardeditor.model.ElementTransform
import com.zcard.feature.cardeditor.util.toBase62
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long
import kotlin.onSuccess

private const val TAG = "transformViewModel"

@HiltViewModel
class TransformViewModel @Inject constructor(
    private val unityBridge: UnityBridge,
    private val cardElementRepository: CardElementRepository,
) : ViewModel() {

    // handleInit의 onSuccess에서만 함께 설정됨 — non-null이면 DB 로드 완료 + tempTransform 초기화 보장
    private var _elementId: Long? = null
    private var _cardId: Long? = null

    private val _transformState = MutableStateFlow(TransformState())
    val transformState: StateFlow<TransformState> = _transformState

    private val _transformSideEffect = Channel<TransformSideEffect>(Channel.BUFFERED)
    val transformSideEffect = _transformSideEffect.receiveAsFlow()

    fun onIntent(intent: TransformIntent) {
        when (intent) {
            is TransformIntent.Init -> handleInit(intent.elementId)
            is TransformIntent.MoveObject -> handleMoveObject(intent.direction)
            is TransformIntent.ChangeScale -> handleChangeScale(intent.newScale)
            is TransformIntent.ResetTransform -> handleResetTransform()
            is TransformIntent.ToggleCameraFocus -> handleToggleCameraFocus()
            is TransformIntent.ResetCamera -> handleResetCamera()
            is TransformIntent.Exit -> handleExit()
            is TransformIntent.DismissDialog -> handleDismissDialog()
            is TransformIntent.SaveChanges -> handleSaveChanges()
            is TransformIntent.SaveAndExit -> handleSaveAndExit()
            is TransformIntent.DiscardAndExit -> handleDiscardAndExit()
        }
    }

    private fun handleInit(elementId: Long) {
        viewModelScope.launch {
            cardElementRepository.getObjectWithAssetKeys(elementId)
                .onSuccess { obj ->
                    val transform = ElementTransform(
                        posX = obj.cardElement.posX,
                        posY = obj.cardElement.posY,
                        posZ = obj.cardElement.posZ,
                        scale = obj.cardElement.scale
                    )
                    _elementId = obj.cardElement.elementId
                    _cardId = obj.cardElement.cardId
                    _transformState.update {
                        it.copy(
                            elementKey = toBase62(obj.cardElement.elementId),
                            thumbnailKey = obj.thumbnailKey,
                            initialTransform = transform,
                            tempTransform = transform,
                        )
                    }
                }.onFailure { e ->
                    Log.e(TAG, "handleInit: $e")
                    _transformSideEffect.trySend(TransformSideEffect.ToastMessage(0)) // TODO
                    _transformSideEffect.trySend(TransformSideEffect.Finish)
                }
            }
    }

    // ── 트랜스폼 조작 ──────────────────────────────────────────────────────────

    private fun handleMoveObject(direction: Direction) {
        val elementId = _elementId ?: return
        val temp = _transformState.value.tempTransform
        val updated = ElementTransform(
            posX = temp.posX + direction.dx,
            posY = temp.posY + direction.dy,
            posZ = temp.posZ + direction.dz,
            scale = temp.scale
        )

        _transformState.update {
            it.copy(tempTransform = updated)
        }

        unityBridge.updatePosition(elementId, updated.posX, updated.posY, updated.posZ)
    }

    private fun handleChangeScale(newScale: Int) {
        val elementId = _elementId ?: return
        val temp = _transformState.value.tempTransform
        if(newScale < 1) return

        _transformState.update {
            it.copy(tempTransform = temp.copy(scale = newScale))
        }

        unityBridge.updateScale(elementId, newScale)
    }

    private fun handleResetTransform() {
        val initial = _transformState.value.initialTransform

        _transformState.update {
            it.copy(tempTransform = initial.copy())
        }

        unityResetTransform()
    }

    // ── 카메라 ─────────────────────────────────────────────────────────────────

    private fun handleToggleCameraFocus() {
        val elementId = _elementId ?: return
        val cameraFocus = _transformState.value.isCameraFocus

        if(cameraFocus) {
            unityBridge.clearSelection()
        } else {
            unityBridge.selectObject(elementId)
        }

        _transformState.update { it.copy(isCameraFocus = !cameraFocus) }
    }

    private fun handleResetCamera() {
        unityBridge.clearSelection()
    }

    // ── 화면 흐름 ──────────────────────────────────────────────────────────────

    private fun handleExit() {
        if(_transformState.value.hasPendingTransform) {
            _transformState.update { it.copy(showUnsavedChangesDialog = true) }
        } else {
            _transformSideEffect.trySend(TransformSideEffect.Finish)
        }
    }

    private fun handleDismissDialog() {
        _transformState.update { it.copy(showUnsavedChangesDialog = false) }
    }

    private fun handleSaveChanges() {
        saveTransformChanges()
    }

    private fun handleSaveAndExit() {
        saveTransformChanges {
            _transformState.update { it.copy(showUnsavedChangesDialog = false) }
            _transformSideEffect.trySend(TransformSideEffect.Finish)
        }
    }

    private fun handleDiscardAndExit() {
        unityResetTransform()
        _transformState.update { it.copy(showUnsavedChangesDialog = false) }
        _transformSideEffect.trySend(TransformSideEffect.Finish)
    }

    // ── 공통 유틸 ─────────────────────────────────────────────────────────────

    private fun saveTransformChanges(onSuccess: () -> Unit = {}) {
        val cardId = _cardId ?: return
        val elementId = _elementId ?: return
        val temp = _transformState.value.tempTransform

        viewModelScope.launch {
            cardElementRepository.updateElementTransform(
                cardId = cardId,
                elementId = elementId,
                posX = temp.posX,
                posY = temp.posY,
                posZ = temp.posZ,
                scale = temp.scale
            ).onSuccess {
                onSuccess()
            }.onFailure {
                Log.e(TAG, "saveTransformChanges: $it")
                _transformSideEffect.trySend(TransformSideEffect.ToastMessage(0)) // TODO
            }
        }
    }

    private fun unityResetTransform() {
        val elementId = _elementId ?: return
        val initial = _transformState.value.initialTransform

        unityBridge.updatePosition(elementId, initial.posX, initial.posY, initial.posZ)
        unityBridge.updateScale(elementId, initial.scale)
    }
}