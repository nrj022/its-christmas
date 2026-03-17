package com.zcard.data.usecaseImpl

import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.AuthRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.UploadCardModelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.io.File
import javax.inject.Inject

class UploadCardModelUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val cardRepository: CardRepository,
): UploadCardModelUseCase {

    override suspend fun invoke(cardId: Long, file: File): Flow<UploadState> {
        authRepository.signInAnonymously()

        return cardRepository.uploadGlbToFirebase(file).transform { progress ->
            if(progress is UploadState.Success) {
                cardRepository.updateGlb(cardId, file.name).getOrNull() ?: emit(UploadState.Failure)
            }
            emit(progress)
        }
    }
}