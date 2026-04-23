package com.zcard.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.model.CardPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    cardRepository: CardRepository,
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
}