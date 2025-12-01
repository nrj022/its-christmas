package com.itschristmas.domain.repository

import com.itschristmas.domain.model.Card
import java.io.File

interface CardRepository {

    suspend fun insertCard(card: Card): Long

    suspend fun getCardById(cardId: Long): Card

    suspend fun getGlbKeyAndBackgroundUnityKey(cardId: Long): Pair<String?, String?>

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Int

    suspend fun updateGlbKey(cardId: Long, glbKey: String): Int
}