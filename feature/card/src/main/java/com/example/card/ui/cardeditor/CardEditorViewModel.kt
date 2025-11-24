package com.example.card.ui.cardeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.enum.AssetType
import com.example.domain.enum.ElementType
import com.example.domain.model.Asset
import com.example.domain.model.CardElement
import com.example.domain.repository.AssetRepository
import com.example.domain.repository.CardElementRepository
import com.example.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.Long

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
            is CardEditorIntent.ObjectClicked -> {
                handleObjectClicked(intent.cardId, intent.clickedObject)
            }
            is CardEditorIntent.BackgroundClicked -> {
                handleBackgroundClicked(intent.assetId)
            }
            is CardEditorIntent.MyObjectClicked -> {
                handleMyObjectClicked(intent.assetId)
            }
        }
    }

    private fun handleInit(cardId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getCardDataFromDB(cardId)
                loadMyObjectsFromDB(cardId)
                getObjectsFromDB()
                getBackgroundsFromDB()
            }
        }
    }
    private fun handleObjectClicked(cardId: Long, clickedObject: Asset) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
    }

    private fun handleBackgroundClicked(assetId: Long) {
        _cardEditorState.update {
            it.copy(selectedBackground = if (it.selectedBackground == assetId) null else assetId)
        }
    }

    private fun handleMyObjectClicked(assetId: Long) {
        _cardEditorState.update {
            it.copy(selectedMyObject = if (it.selectedMyObject == assetId) null else assetId)
        }
    }

    suspend fun getCardDataFromDB(cardId: Long) {
        val card = cardRepository.getCardById(cardId)
        _cardEditorState.update {
            it.copy(selectedBackground = card.backgroundAssetId)
        }
    }

    private fun loadMyObjectsFromDB(cardId: Long) {
        viewModelScope.launch {
            cardElementRepository.getObjectElementsByCardId(cardId)
                .collect {
                    _cardEditorState.update { state ->
                        state.copy(myObjects = it)
                    }
                }
        }
    }

    suspend fun getObjectsFromDB() {
        val objects = assetRepository.getAssetsByType(AssetType.OBJECT)
        _cardEditorState.update {
            it.copy(objects = objects)
        }
    }

    suspend fun getBackgroundsFromDB() {
        val backgrounds = assetRepository.getAssetsByType(AssetType.BACKGROUND)
        _cardEditorState.update {
            it.copy(backgrounds = backgrounds)
        }
    }
}