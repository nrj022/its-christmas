package com.itschristmas.domain.usecase

interface GenerateCardUrlUseCase {
    suspend operator fun invoke(cardId: Long): String
}
