package com.zcard.domain.usecase

interface CreateCardUseCase {
    suspend operator fun invoke(): Result<Long>
}
