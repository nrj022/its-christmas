package com.itschristmas.domain.repository

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.CardElement
import kotlinx.coroutines.flow.Flow

interface CardElementRepository {

    suspend fun insertCardElements(cardElements: List<CardElement>): Result<List<Long>>

    suspend fun insertCardElement(cardElement: CardElement): Result<Long>

    suspend fun deleteCardElementsByCardId(cardId: Long): Result<Int>

    suspend fun deleteCardElementsById(elementId: Long): Result<Int>

    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<Result<List<CardElementWithAssetKeys>>>

    suspend fun getTextElementsByCardId(cardId: Long): Result<List<CardElement>>

    suspend fun updateElementPosition(elementId: Long, posX: Int, posY: Int): Result<Int>

    suspend fun updateElementScale(elementId: Long, scale: Int): Result<Int>

    suspend fun updateTextFontSize(elementId: Long, fontSize: Float): Result<Int>

    suspend fun updateTextColor(elementId: Long, textColor: String): Result<Int>

    suspend fun updateTextContent(elementId: Long, textContent: String): Result<Int>

    suspend fun updateTextFont(elementId: Long, fontFamily: String): Result<Int>

    suspend fun updateTextAlign(elementId: Long, textAlign: String): Result<Int>
}