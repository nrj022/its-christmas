package com.zcard.domain.usecase

interface InitCardEditorUseCase {
    suspend operator fun invoke(cardId: Long): Result<LoadCardEditorResult>
}