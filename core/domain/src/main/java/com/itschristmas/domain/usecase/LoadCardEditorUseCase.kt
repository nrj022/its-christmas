package com.itschristmas.domain.usecase

import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.Card
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.TextElement
import kotlinx.coroutines.flow.Flow

data class LoadCardEditorResult(
    val cardData: Card,
    val objects: List<Asset>,
    val backgrounds: List<Asset>,
    val texts: List<TextElement>,
    val spawnedObjectsFlow: Flow<Result<List<CardElementWithAssetKeys>>>
)

interface LoadCardEditorUseCase {
    suspend operator fun invoke(cardId: Long): Result<LoadCardEditorResult>
}