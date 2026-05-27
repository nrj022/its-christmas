package com.zcard.data.usecaseImpl

import android.util.Log
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.GetCardUseCase
import com.zcard.domain.usecase.GetCardUseCaseResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "GetCardUseCaseImpl"

class GetCardUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val cardElementRepository: CardElementRepository,
    private val assetRepository: AssetRepository,
    private val generateCardUrlUseCase: GenerateCardUrlUseCase
): GetCardUseCase {
    override suspend fun invoke(cardId: Long): Result<GetCardUseCaseResult> {
        return coroutineScope {
            runCatching {
                val cardDataDeferred = async { cardRepository.getCardById(cardId).getOrThrow() }
                val objectsDeferred = async { assetRepository.getObjects().getOrThrow() }
                val backgroundsDeferred = async { assetRepository.getBackgrounds().getOrThrow() }
                val textsDeferred = async { cardElementRepository.getTextElementsByCardId(cardId).getOrThrow() }
                val spawnedObjectsFlow =
                    cardElementRepository.getObjectElementsWithAssetKeysByCardId(cardId)
                val spawnedObjectsDeferred = async { spawnedObjectsFlow.first().getOrThrow() }

                val cardUrlDeferred = async { generateCardUrlUseCase(cardId).getOrNull() ?: "" }

                GetCardUseCaseResult(
                    cardData = cardDataDeferred.await(),
                    cardUrl = cardUrlDeferred.await(),
                    objects = objectsDeferred.await(),
                    backgrounds = backgroundsDeferred.await(),
                    texts = textsDeferred.await(),
                    spawnedObjects = spawnedObjectsDeferred.await(),
                    spawnedObjectsFlow = spawnedObjectsFlow
                )
            }.onFailure {
                Log.e(TAG, "invoke failed: $it")
            }
        }
    }
}