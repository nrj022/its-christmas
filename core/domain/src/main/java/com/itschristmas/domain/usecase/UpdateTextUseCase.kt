package com.itschristmas.domain.usecase

import com.itschristmas.domain.model.TextElement

data class UpdateTextParams(
    val cardId: Long,
    val updates: List<TextElement>,
    val deletedIds: Set<Long> = emptySet()
)

data class UpdateTextResult(
    val updatedElements: List<TextElement>,
    val failedUpdates: List<TextElement> = emptyList(),
    val deletedIds: Set<Long> = emptySet()
)

interface UpdateTextUseCase {
    suspend operator fun invoke(params: UpdateTextParams): Result<UpdateTextResult>
}