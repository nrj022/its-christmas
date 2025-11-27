package com.example.card.ui.cardeditor

import android.util.Log
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        getCardDataFromDB(cardId)
        getObjectsFromDB()
        getBackgroundsFromDB()

        // MyObjects Flow 구독
        loadMyObjectsFromDB(cardId)
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

    private fun handleMyObjectClicked(assetId: Long) {
        _cardEditorState.update {
            it.copy(selectedMyObject = if (it.selectedMyObject == assetId) null else assetId)
        }
    }

    private fun getCardDataFromDB(cardId: Long) {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId)
            _cardEditorState.update {
                it.copy(selectedBackground = card.backgroundAssetId)
            }
        }
    }
    private fun getObjectsFromDB() {
        viewModelScope.launch {
            val objects = assetRepository.getAssetsByType(AssetType.OBJECT)
            _cardEditorState.update {
                it.copy(objects = objects)
            }
        }
    }

    private fun getBackgroundsFromDB() {
        viewModelScope.launch {
            val backgrounds = assetRepository.getAssetsByType(AssetType.BACKGROUND)
            _cardEditorState.update {
                it.copy(backgrounds = backgrounds)
            }
        }
    }

    private fun loadMyObjectsFromDB(cardId: Long) {
        viewModelScope.launch {
            cardElementRepository.getObjectElementsByCardId(cardId)
                .collect {
                    loadAssetThumbMap(it)
                    _cardEditorState.update { state ->
                        state.copy(myObjects = it)
                    }
                }
        }
    }

    private suspend fun loadAssetThumbMap(objects: List<CardElement>) {
        val results: List<Pair<Long, String>>? = withContext(Dispatchers.IO) {
            val objectIds = objects.mapNotNull { it.assetId }.distinct()
            val currentMap = _cardEditorState.value.assetThumbMap.toMutableMap()

            val missingIds = objectIds.filter { it !in currentMap.keys }
            if(missingIds.isEmpty()) null

            coroutineScope {
                missingIds.map { id ->
                    async {
                        try {
                            val asset = assetRepository.getAssetById(id)
                            val thumb = asset.thumbnailKey
                            if(thumb.isNotEmpty()) id to thumb else null
                        } catch (e: Exception) {
                            Log.e(TAG, "failed to fetch assets", e)
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
        }

        if(results == null) return

        _cardEditorState.update {
            it.copy(assetThumbMap = it.assetThumbMap + results.toMap())
        }
    }
}