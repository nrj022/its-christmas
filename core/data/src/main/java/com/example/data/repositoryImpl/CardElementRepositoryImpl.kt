package com.example.data.repositoryImpl

import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.database.dao.CardElementDao
import com.example.domain.model.CardElement
import com.example.domain.repository.CardElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CardElementRepositoryImpl @Inject constructor(
    private val cardElementDao: CardElementDao
): CardElementRepository {
    override suspend fun insertCardElements(cardElements: List<CardElement>) {
        withContext(Dispatchers.IO) {
            cardElementDao.insertAll(cardElements.map { it.toEntity() })
        }
    }

    override suspend fun insertCardElement(cardElement: CardElement): Long {
        return withContext(Dispatchers.IO) {
            cardElementDao.insertElement(cardElement.toEntity())
        }
    }

    override suspend fun deleteCardElementsByCardId(cardId: Long) {
        withContext(Dispatchers.IO) {
            cardElementDao.deleteByCardId(cardId)
        }
    }

    override fun getObjectElementsByCardId(cardId: Long): Flow<List<CardElement>> {
        return cardElementDao.getObjectElementsByCardId(cardId)
            .map { entityList -> entityList.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getTextElementsByCardId(cardId: Long): List<CardElement> {
        return withContext(Dispatchers.IO) {
            cardElementDao.getTextElementsByCardId(cardId).map { it.toDomain() }
        }
    }
}