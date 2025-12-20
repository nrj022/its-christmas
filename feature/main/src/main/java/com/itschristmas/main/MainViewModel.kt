package com.itschristmas.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.itschristmas.domain.model.Card
import com.itschristmas.domain.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cardRepository: CardRepository
): ViewModel() {
    private val _cardList = MutableStateFlow<List<Card>>(emptyList())
    val cardList: StateFlow<List<Card>> = _cardList

    suspend fun getAllCardsFromDB() {
        cardRepository.getAllCards()
            .onSuccess {
                _cardList.value = it
            }
            .onFailure {
                Log.e(TAG, "getAllCardsFromDB: $it")
                // 에러 처리
            }
    }
}