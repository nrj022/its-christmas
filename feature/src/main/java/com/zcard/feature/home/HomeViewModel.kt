package com.zcard.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.model.CardPreview
import com.zcard.domain.model.UnityEventType
import com.zcard.domain.model.UnityMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cardRepository: CardRepository,
): ViewModel() {

    val cardPreviews: StateFlow<List<CardPreview>> = cardRepository.getCardPreviews()
        .transform { result ->
            result.onSuccess { emit(it) }
                .onFailure { e ->
                    Log.e(TAG, "getCardPreviewsFromDB: $e")
                    // TODO: 실패 시 처리
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun handleUnityMessage(msg: UnityMessage) {
        if(msg.type == UnityEventType.CREATE_CARD_THUMB) {
            val cardId = msg.data.toLongOrNull() ?: return
            if(cardId > 0) viewModelScope.launch { cardRepository.updateCardThumbnail(cardId) }
        }
    }
}