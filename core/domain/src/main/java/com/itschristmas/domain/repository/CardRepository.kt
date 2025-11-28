package com.itschristmas.domain.repository

import com.itschristmas.domain.model.Card
import java.io.File

interface CardRepository {

    suspend fun insertCard(card: Card): Long

    suspend fun getCardById(cardId: Long): Card

    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Int)

    suspend fun uploadGlbFileToFirebase(glbFile: File): String
}