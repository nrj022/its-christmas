package com.zcard.data.usecaseImpl

import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.AuthRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.UploadCardModelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import java.io.File
import javax.inject.Inject

class UploadCardModelUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val cardRepository: CardRepository,
): UploadCardModelUseCase {

    override suspend fun invoke(cardId: Long, file: File): Flow<UploadState> {
        val authResult = authRepository.signInAnonymously()
        if (authResult.isFailure) return flowOf(UploadState.Failure)

        return cardRepository.uploadGlbToFirebase(file).transform { progress ->
            if(progress is UploadState.Success) {
                val updatedRows = cardRepository.updateGlb(cardId, file.name).getOrNull() ?: 0
                if (updatedRows > 0) {
                    emit(UploadState.Success)
                } else {
                    emit(UploadState.Failure)
                }
                return@transform
            }
            emit(progress)
        }
    }
}