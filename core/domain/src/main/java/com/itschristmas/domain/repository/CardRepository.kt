package com.itschristmas.domain.repository

import com.itschristmas.domain.model.Card
import com.itschristmas.domain.model.GlbAndBgFirebase

interface CardRepository {

    suspend fun insertCard(card: Card): Result<Long>

    suspend fun getAllCards(): Result<List<Card>>

    suspend fun getCardById(cardId: Long): Result<Card>

    suspend fun getGlbAndBgFirebase(cardId: Long): Result<GlbAndBgFirebase>

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int>

    suspend fun updateGlb(cardId: Long, glbFileName: String, glbToken: String): Result<Int>
}