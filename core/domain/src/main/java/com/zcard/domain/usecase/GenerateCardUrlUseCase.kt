package com.zcard.domain.usecase

interface GenerateCardUrlUseCase {
    suspend operator fun invoke(cardId: Long): String
}
