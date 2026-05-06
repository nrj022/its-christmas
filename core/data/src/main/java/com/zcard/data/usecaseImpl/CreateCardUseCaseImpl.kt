package com.zcard.data.usecaseImpl

import android.util.Log
import com.zcard.domain.model.Card
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.CreateCardUseCase
import javax.inject.Inject

private const val TAG = "CreateCardUseCaseImpl"

class CreateCardUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
): CreateCardUseCase {

    override suspend fun invoke(): Long {
        var cardId = -1L
        cardRepository.insertCard(Card())
            .onSuccess {
                cardRepository.updateCardTitle(it, "New Card $it")
                    .onFailure { e -> Log.w(TAG, "updateCardTitle failed: $e") }
                cardId = it
            }.onFailure { e ->
                Log.e(TAG, "insertCard failed: $e")
            }
        return cardId
    }
}