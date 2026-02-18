package com.itschristmas.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardRepository
import com.itschristmas.main.model.CardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val assetRepository: AssetRepository
): ViewModel() {

    private val _cardList = MutableStateFlow<List<CardItem>>(emptyList())
    val cardList: StateFlow<List<CardItem>> = _cardList

    private var backgroundList = emptyList<Asset>()

    init {
        loadBackgrounds()
    }

    fun loadBackgrounds() {
        backgroundList = assetRepository.getInitialBackgrounds()
    }

    fun loadCards() {
        viewModelScope.launch {
            cardRepository.getAllCards()
                .onSuccess { cards ->
                    _cardList.value = cards.map {
                        val thumbnailKey =
                            backgroundList.find { bg -> bg.assetId == it.backgroundAssetId }?.thumbnailKey
                        CardItem(it, thumbnailKey)
                    }
                }
                .onFailure {
                    Log.e(TAG, "getAllCardsFromDB: $it")
                    // 에러 처리
                }
        }
    }
}