package com.itschristmas.domain.repository

import com.itschristmas.domain.model.CardElement
import kotlinx.coroutines.flow.Flow

interface CardElementRepository {

    suspend fun insertCardElements(cardElements: List<CardElement>)

    suspend fun insertCardElement(cardElement: CardElement): Long

    suspend fun deleteCardElementsByCardId(cardId: Long)

    fun getObjectElementsByCardId(cardId: Long): Flow<List<CardElement>>

    suspend fun getTextElementsByCardId(cardId: Long): List<CardElement>
}