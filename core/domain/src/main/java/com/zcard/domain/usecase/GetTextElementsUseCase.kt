package com.zcard.domain.usecase

import com.zcard.domain.model.TextElement

interface GetTextElementsUseCase {
    suspend operator fun invoke(cardId: Long): Result<List<TextElement>>
}