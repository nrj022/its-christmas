package com.itschristmas.domain.repository

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.CardElement
import kotlinx.coroutines.flow.Flow

interface CardElementRepository {

    suspend fun insertCardElements(cardElements: List<CardElement>): List<Long>

    suspend fun insertCardElement(cardElement: CardElement): Long

    suspend fun deleteCardElementsByCardId(cardId: Long): Int

    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<List<CardElementWithAssetKeys>>

    suspend fun getTextElementsByCardId(cardId: Long): List<CardElement>

    suspend fun updateElementPosition(elementId: Long, posX: Int, posY: Int): Int

    suspend fun updateElementScale(elementId: Long, scale: Int): Int

    suspend fun updateTextFontSize(elementId: Long, fontSize: Float): Int

    suspend fun updateTextColor(elementId: Long, textColor: String): Int

    suspend fun updateTextContent(elementId: Long, textContent: String): Int

    suspend fun updateTextFont(elementId: Long, fontFamily: String): Int

    suspend fun updateTextAlign(elementId: Long, textAlign: String): Int
}