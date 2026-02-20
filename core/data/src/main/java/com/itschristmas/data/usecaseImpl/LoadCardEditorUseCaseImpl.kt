package com.itschristmas.data.usecaseImpl

import android.util.Log
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.repository.AssetRepository
import com.itschristmas.domain.repository.CardElementRepository
import com.itschristmas.domain.repository.CardRepository
import com.itschristmas.domain.usecase.GenerateCardUrlUseCase
import com.itschristmas.domain.usecase.GetTextElementsUseCase
import com.itschristmas.domain.usecase.LoadCardEditorResult
import com.itschristmas.domain.usecase.LoadCardEditorUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

private const val TAG = "LoadCardEditorUseCaseImpl"

class LoadCardEditorUseCaseImpl @Inject constructor(
    private val cardRepository: CardRepository,
    private val cardElementRepository: CardElementRepository,
    private val assetRepository: AssetRepository,
    private val getTextsUseCase: GetTextElementsUseCase,
    private val generateCardUrlUseCase: GenerateCardUrlUseCase
): LoadCardEditorUseCase {

    override suspend fun invoke(cardId: Long): Result<LoadCardEditorResult> {
        return coroutineScope {
            runCatching {
                val cardDataDeferred = async { cardRepository.getCardById(cardId).getOrThrow() }
                val objectsDeferred = async { assetRepository.getAssetsByType(AssetType.OBJECT).getOrThrow() }
                val backgroundsDeferred = async { assetRepository.getAssetsByType(AssetType.BACKGROUND).getOrThrow() }
                val textsDeferred = async { getTextsUseCase(cardId).getOrThrow() }
                val spawnedObjectsFlow =
                    cardElementRepository.getObjectElementsWithAssetKeysByCardId(cardId)

                val cardUrl = generateCardUrlUseCase(cardId)

                LoadCardEditorResult(
                    cardData = cardDataDeferred.await(),
                    cardUrl = cardUrl,
                    objects = objectsDeferred.await(),
                    backgrounds = backgroundsDeferred.await(),
                    texts = textsDeferred.await(),
                    spawnedObjectsFlow = spawnedObjectsFlow
                )
            }.onFailure {
                Log.e(TAG, "invoke failed: $it")
            }
        }
    }
}