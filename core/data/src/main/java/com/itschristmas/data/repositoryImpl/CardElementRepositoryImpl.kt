package com.itschristmas.data.repositoryImpl

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.mapper.toEntity
import com.itschristmas.data.repositoryImpl.common.ioCatching
import com.itschristmas.database.dao.CardElementDao
import com.itschristmas.domain.model.CardElement
import com.itschristmas.domain.model.TextAttributes
import com.itschristmas.domain.repository.CardElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardElementRepositoryImpl @Inject constructor(
    private val cardElementDao: CardElementDao
): CardElementRepository {
    override suspend fun insertCardElements(cardElements: List<CardElement>): Result<List<Long>> =
        ioCatching {
            cardElementDao.insertAll(cardElements.map { it.toEntity() })
        }

    override suspend fun insertCardElement(cardElement: CardElement): Result<Long> =
        ioCatching {
            cardElementDao.insertElement(cardElement.toEntity())
        }

    override suspend fun deleteCardElementsByCardId(cardId: Long): Result<Int> =
        ioCatching {
            cardElementDao.deleteByCardId(cardId)
        }

    override suspend fun deleteCardElementById(elementId: Long): Result<Int> =
        ioCatching {
            cardElementDao.deleteElementById(elementId)
        }

    override suspend fun deleteCardElementsByIds(elementIds: List<Long>): Result<Int> =
        ioCatching {
            cardElementDao.deleteElementsByIds(elementIds)
        }

    override fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<Result<List<CardElementWithAssetKeys>>> {
        return cardElementDao.getObjectElementsWithAssetKeysByCardId(cardId)
            .map { entityList ->
                Result.success(entityList.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getTextElementsByCardId(cardId: Long): Result<List<CardElement>> =
        ioCatching {
            cardElementDao.getTextElementsByCardId(cardId).map { it.toDomain() }
        }

    override suspend fun updateElementTransform(elementId: Long, posX: Float, posY: Float, scale: Int): Result<Int> =
        ioCatching {
            cardElementDao.updateElementTransform(elementId, posX, posY, scale)
        }

    override suspend fun updateTextElement(elementId: Long, textAttributes: TextAttributes, posX: Float, posY: Float): Result<Int> =
        ioCatching {
            cardElementDao.updateTextElement(
                elementId = elementId,
                textContent = textAttributes.content,
                textAlign = textAttributes.alignment.name,
                textColor = textAttributes.textColor.name,
                fontSize = textAttributes.fontSize,
                fontFamily = textAttributes.fontFamily.name,
                posX = posX,
                posY = posY
            )
        }
}