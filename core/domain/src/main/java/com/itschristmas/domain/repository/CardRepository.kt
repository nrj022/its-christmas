package com.itschristmas.domain.repository

import com.itschristmas.domain.model.Card
import com.itschristmas.domain.model.GlbAndBgFirebaseKey

interface CardRepository {

    suspend fun insertCard(card: Card): Result<Long>

    suspend fun getCardById(cardId: Long): Result<Card>

    suspend fun getGlbAndBgFirebaseKey(cardId: Long): Result<GlbAndBgFirebaseKey>

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int>

    suspend fun updateGlbKey(cardId: Long, glbKey: String): Result<Int>
}