package com.zcard.data.usecaseImpl

import android.util.Log
import com.zcard.domain.model.Card
import com.zcard.domain.repository.AssetRepository
import com.zcard.domain.repository.CardElementRepository
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import com.zcard.domain.usecase.OpenCardEditorUseCase
import com.zcard.domain.usecase.OpenCardEditorUseCaseResult
import com.zcard.domain.usecase.GetTextElementsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "OpenCardEditorUseCaseImpl"

class OpenCardEditorUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val cardElementRepository: CardElementRepository,
    private val assetRepository: AssetRepository,
    private val getTextsUseCase: GetTextElementsUseCase,
    private val generateCardUrlUseCase: GenerateCardUrlUseCase
): OpenCardEditorUseCase {

    override suspend fun invoke(cardId: Long): Result<OpenCardEditorUseCaseResult> {
        return runCatching {
            val resolvedCardId = if (cardId < 0) {
                cardRepository.insertCard(Card())
            } else {
                Result.success(cardId)
            }.getOrElse { return Result.failure(it) }

            return coroutineScope {
                runCatching {
                    val cardDataDeferred = async { cardRepository.getCardById(resolvedCardId).getOrThrow() }
                    val objectsDeferred = async { assetRepository.getObjects().getOrThrow() }
                    val backgroundsDeferred = async { assetRepository.getBackgrounds().getOrThrow() }
                    val textsDeferred = async { getTextsUseCase(resolvedCardId).getOrThrow() }
                    val spawnedObjectsFlow =
                        cardElementRepository.getObjectElementsWithAssetKeysByCardId(resolvedCardId)
                    val spawnedObjectsDeferred = async { spawnedObjectsFlow.first().getOrThrow() }

                    val cardUrlDeferred = async { generateCardUrlUseCase(resolvedCardId) }

                    OpenCardEditorUseCaseResult(
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
}