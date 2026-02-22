package com.zcard.data.usecaseImpl

import com.zcard.domain.model.Card
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.InitCardEditorUseCase
import com.zcard.domain.usecase.LoadCardEditorResult
import com.zcard.domain.usecase.LoadCardEditorUseCase
import javax.inject.Inject

class InitCardEditorUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val loadCardEditorUseCase: LoadCardEditorUseCase
): InitCardEditorUseCase {

    override suspend fun invoke(cardId: Long): Result<LoadCardEditorResult> {
        return runCatching {
            val id = if (cardId < 0) {
                cardRepository.insertCard(Card())
            } else {
                Result.success(cardId)
            }.getOrElse { return Result.failure(it) }

            return loadCardEditorUseCase(id)
        }
    }
}