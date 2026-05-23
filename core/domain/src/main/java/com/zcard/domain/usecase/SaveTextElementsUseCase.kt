package com.zcard.domain.usecase

import com.zcard.domain.model.TextElement

data class SaveTextElementsParams(
    val cardId: Long,
    val updates: List<TextElement>,
    val deletedIds: Set<Long> = emptySet()
)

data class SaveTextElementsResult(
    val updatedElements: List<TextElement>,
    val failedUpdates: List<TextElement> = emptyList(),
    val failedDeleteIds: Set<Long> = emptySet()
)

interface SaveTextElementsUseCase {
    suspend operator fun invoke(params: SaveTextElementsParams): Result<SaveTextElementsResult>
}