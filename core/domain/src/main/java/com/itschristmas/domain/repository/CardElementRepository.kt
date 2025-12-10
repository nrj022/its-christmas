package com.itschristmas.domain.repository

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.TextAttributes
import kotlinx.coroutines.flow.Flow

interface CardElementRepository {

    suspend fun insertCardElements(cardElements: List<CardElement>): Result<List<Long>>

    suspend fun insertCardElement(cardElement: CardElement): Result<Long>

    suspend fun deleteCardElementsByCardId(cardId: Long): Result<Int>

    suspend fun deleteCardElementById(elementId: Long): Result<Int>

    suspend fun deleteCardElementsByIds(elementIds: List<Long>): Result<Int>

    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<Result<List<CardElementWithAssetKeys>>>

    suspend fun getTextElementsByCardId(cardId: Long): Result<List<CardElement>>

    suspend fun updateElementTransform(elementId: Long, posX: Int, posY: Int, scale: Int): Result<Int>

    suspend fun updateTextElement(elementId: Long, textAttributes: TextAttributes, posX: Int, posY: Int): Result<Int>
}