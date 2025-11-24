package com.example.domain.repository

import com.example.domain.model.CardElement

interface CardElementRepository {

    suspend fun insertCardElements(cardElements: List<CardElement>)

    suspend fun deleteCardElementsByCardId(cardId: Long)

    suspend fun getCardElementsByCardId(cardId: Long): List<CardElement>

    suspend fun getTextElementsByCardId(cardId: Long): List<CardElement>
}