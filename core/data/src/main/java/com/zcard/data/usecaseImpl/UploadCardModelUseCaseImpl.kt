package com.zcard.data.usecaseImpl

import android.util.Log
import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.AuthRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.UploadCardModelUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

private const val TAG = "UploadCardModelUseCaseImpl"

class UploadCardModelUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val cardRepository: CardRepository,
): UploadCardModelUseCase {

    override suspend fun invoke(cardId: Long, fileName: String): Flow<UploadState> {
        val authResult = authRepository.signInAnonymously()
        if (authResult.isFailure) return flowOf(UploadState.Failure(Throwable("Failed to Anonymous sign in")))

        return cardRepository.uploadGlbToFirebase(fileName).transform { progress ->
            if(progress is UploadState.Success) {
                val updatedRows = cardRepository.updateGlb(cardId, fileName).getOrNull() ?: 0
                if (updatedRows > 0) {
                    if (!cardRepository.deleteCardGlb(fileName)) {
                        Log.w(TAG, "Failed to delete card glb file")
                    }
                    emit(UploadState.Success)
                } else {
                    emit(UploadState.Failure(Throwable("Failed to update card data")))
                }
                return@transform
            }
            emit(progress)
        }
    }
}