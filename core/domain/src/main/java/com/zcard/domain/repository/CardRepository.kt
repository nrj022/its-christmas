package com.zcard.domain.repository

import com.zcard.domain.model.Card
import com.zcard.domain.model.CardPreview
import com.zcard.domain.model.UploadState
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun insertCard(card: Card): Result<Long>

    suspend fun deleteCard(cardId: Long): Result<Int>

    fun getCardPreviews(): Flow<Result<List<CardPreview>>>

    suspend fun getCardById(cardId: Long): Result<Card>

    suspend fun updateCardThumbnail(cardId: Long): Result<Int>

    suspend fun updateCardTitle(cardId: Long, title: String): Result<Int>

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int>

    suspend fun updateGlb(cardId: Long, glbFileName: String): Result<Int>

    suspend fun deleteCardGlb(fileName: String): Result<Unit>

    suspend fun uploadGlbToFirebase(fileName: String): Flow<UploadState>
}