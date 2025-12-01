package com.itschristmas.data.repositoryImpl

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.mapper.toEntity
import com.itschristmas.database.dao.CardElementDao
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.repository.CardElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CardElementRepositoryImpl @Inject constructor(
    private val cardElementDao: CardElementDao
): CardElementRepository {
    override suspend fun insertCardElements(cardElements: List<CardElement>): List<Long> {
        return withContext(Dispatchers.IO) {
            cardElementDao.insertAll(cardElements.map { it.toEntity() })
        }
    }

    override suspend fun insertCardElement(cardElement: CardElement): Long {
        return withContext(Dispatchers.IO) {
            cardElementDao.insertElement(cardElement.toEntity())
        }
    }

    override suspend fun deleteCardElementsByCardId(cardId: Long): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.deleteByCardId(cardId)
        }
    }

    override fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<List<CardElementWithAssetKeys>> {
        return cardElementDao.getObjectElementsWithAssetKeysByCardId(cardId)
            .map { entityList -> entityList.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getTextElementsByCardId(cardId: Long): List<CardElement> {
        return withContext(Dispatchers.IO) {
            cardElementDao.getTextElementsByCardId(cardId).map { it.toDomain() }
        }
    }

    override suspend fun updateElementPosition(elementId: Long, posX: Int, posY: Int): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateElementPosition(elementId, posX, posY)
        }
    }

    override suspend fun updateElementScale(elementId: Long, scale: Int): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateElementScale(elementId, scale)
        }
    }

    override suspend fun updateTextFontSize(elementId: Long, fontSize: Float): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateTextFontSize(elementId, fontSize)
        }
    }

    override suspend fun updateTextColor(elementId: Long, textColor: String): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateTextColor(elementId, textColor)
        }
    }

    override suspend fun updateTextContent(elementId: Long, textContent: String): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateTextContent(elementId, textContent)
        }
    }

    override suspend fun updateTextFont(elementId: Long, fontFamily: String): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateTextFont(elementId, fontFamily)
        }
    }

    override suspend fun updateTextAlign(elementId: Long, textAlign: String): Int {
        return withContext(Dispatchers.IO) {
            cardElementDao.updateTextAlign(elementId, textAlign)
        }
    }
}