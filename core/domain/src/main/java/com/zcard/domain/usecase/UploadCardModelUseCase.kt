package com.zcard.domain.usecase

import com.zcard.domain.model.UploadState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UploadCardModelUseCase {
    suspend operator fun invoke(cardId: Long, file: File): Flow<UploadState>
}