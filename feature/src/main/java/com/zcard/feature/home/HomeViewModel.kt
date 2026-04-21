package com.zcard.feature.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.model.Asset
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.feature.home.model.CardItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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

    // TODO: Flow로 전체 카드 리스트 조회 방식 변경
    fun loadCards() {
        viewModelScope.launch {
            cardRepository.getAllCards()
                .onSuccess { cards ->
                    _cardList.value = cards.map {
                        val thumbnailKey = "thumb_card_${it.cardId}.jpg"
                        val thumbnailFile = File(context.filesDir, thumbnailKey)
                            .takeIf { file -> file.exists() }
                        val backgroundKey =
                            backgroundList.find { bg -> bg.assetId == it.backgroundAssetId }?.thumbnailKey
                        CardItem(card = it, thumbnailFile = thumbnailFile, backgroundKey = backgroundKey)
                    }
                }
                .onFailure {
                    Log.e(TAG, "getAllCardsFromDB: $it")
                    // 에러 처리
                }
        }
    }
}