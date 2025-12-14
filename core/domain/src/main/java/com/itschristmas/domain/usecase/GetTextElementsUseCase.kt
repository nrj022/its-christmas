package com.itschristmas.domain.usecase

import com.itschristmas.domain.model.TextElement

interface GetTextElementsUseCase {
    suspend operator fun invoke(cardId: Long): Result<List<TextElement>>
}