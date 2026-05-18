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

    override suspend operator fun invoke(): Result<Long> {
        return cardRepository.insertCard(Card())
            .onSuccess {
                cardRepository.updateCardTitle(it, "New Card $it")
                    .onFailure { e -> Log.w(TAG, "updateCardTitle failed: $e") }
            }
            .onFailure { e -> Log.e(TAG, "insertCard failed: $e") }
    }
}
