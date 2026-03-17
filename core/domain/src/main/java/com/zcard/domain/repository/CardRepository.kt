package com.zcard.domain.repository

import com.zcard.domain.model.Card
import com.zcard.domain.model.UploadState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface CardRepository {

    suspend fun insertCard(card: Card): Result<Long>

    suspend fun getAllCards(): Result<List<Card>>

    suspend fun getCardById(cardId: Long): Result<Card>

    suspend fun updateCardTitle(cardId: Long, title: String): Result<Int>

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int>

    suspend fun updateGlb(cardId: Long, glbFileName: String): Result<Int>

    suspend fun uploadGlbToFirebase(file: File): Flow<UploadState>
}