package com.zcard.domain.repository

import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.TextAttributes
import kotlinx.coroutines.flow.Flow

interface CardElementRepository {

    suspend fun insertCardElement(cardElement: CardElement): Result<Long>

    suspend fun deleteCardElementsByCardId(cardId: Long): Result<Int>

    suspend fun deleteCardElementById(cardId: Long, elementId: Long): Result<Int>

    suspend fun deleteCardElementsByIds(cardId: Long, elementIds: List<Long>): Result<Int>

    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<Result<List<CardElementWithAssetKeys>>>

    suspend fun getTextElementsByCardId(cardId: Long): Result<List<CardElement>>

    suspend fun updateElementTransform(cardId: Long, elementId: Long, posX: Float, posY: Float, posZ: Float, scale: Int): Result<Int>

    suspend fun updateTextElement(cardId: Long, elementId: Long, textAttributes: TextAttributes, posX: Float, posY: Float, posZ: Float): Result<Int>
}