package com.zcard.domain.usecase

import com.zcard.domain.model.UploadState
import kotlinx.coroutines.flow.Flow

interface UploadCardModelUseCase {
    suspend operator fun invoke(cardId: Long, fileName: String): Flow<UploadState>
}