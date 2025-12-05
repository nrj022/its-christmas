package com.itschristmas.card.cardeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.card.cardeditor.model.PanelState
import com.itschristmas.card.cardeditor.model.TempTransformState
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.enum.ElementType
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Long

private const val TAG = "CardEditorViewModel"

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val cardElementRepository: CardElementRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _cardEditorState = MutableStateFlow(CardEditorState())
    val cardEditorState: StateFlow<CardEditorState> = _cardEditorState

    fun onIntent(intent: CardEditorIntent) {
        when(intent) {
            is CardEditorIntent.Init -> {
                handleInit(intent.cardId)
            }
            is CardEditorIntent.CardTitleChanged -> {
                handleCardTitleChanged(intent.newTitle)
            }
            is CardEditorIntent.CardGenerateClicked -> {
                handleCardGenerateClicked()
            }
            is CardEditorIntent.ObjectClicked -> {
                handleObjectClicked(intent.cardId, intent.clickedObject)
            }
            is CardEditorIntent.BackgroundClicked -> {
                handleBackgroundClicked(intent.assetId)
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
            is CardEditorIntent.DirectionalClicked -> {
                handleDirectionalClicked(intent.direction)
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

    private fun handleCardGenerateClicked() {
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
                cardElementRepository.deleteCardElementsById(it)
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

    private fun handleDirectionalClicked(direction: Direction) {
        val tempState = _cardEditorState.value.tempTransformState ?: return
        _cardEditorState.update {
            it.copy(tempTransformState =
                when (direction) {
                    Direction.UP -> tempState.copy(posY = tempState.posY + 1)
                    Direction.DOWN -> tempState.copy(posY = tempState.posY - 1)
                    Direction.LEFT -> tempState.copy(posX = tempState.posX - 1)
                    Direction.RIGHT -> tempState.copy(posX = tempState.posX + 1)
                }
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
            cardElementRepository.updateElementPosition(
                elementId = tempState.elementId,
                posX = tempState.posX,
                posY = tempState.posY
            )
            cardElementRepository.updateElementScale(
                elementId = tempState.elementId,
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