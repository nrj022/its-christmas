package com.zcard.domain.usecase

import com.zcard.domain.model.Asset
import com.zcard.domain.model.Card
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.TextElement
import kotlinx.coroutines.flow.Flow

data class GetCardUseCaseResult(
    val cardData: Card,
    val cardUrl: String,
    val objects: List<Asset>,
    val backgrounds: List<Asset>,
    val texts: List<TextElement>,
    val spawnedObjects: List<CardElementWithAssetKeys>,
    val spawnedObjectsFlow: Flow<Result<List<CardElementWithAssetKeys>>>
)

interface GetCardUseCase {
    suspend operator fun invoke(cardId: Long): Result<GetCardUseCaseResult>
}