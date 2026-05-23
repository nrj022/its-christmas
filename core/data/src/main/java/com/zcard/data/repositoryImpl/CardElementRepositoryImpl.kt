package com.zcard.data.repositoryImpl

import androidx.room.withTransaction
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.data.mapper.toDomain
import com.zcard.data.mapper.toEntity
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.AppDatabase
import com.zcard.database.dao.CardDao
import com.zcard.database.dao.CardElementDao
import com.zcard.domain.model.CardElement
import com.zcard.domain.model.TextAttributes
import com.zcard.domain.model.TextElement
import com.zcard.domain.repository.CardElementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CardElementRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val cardDao: CardDao,
    private val cardElementDao: CardElementDao
): CardElementRepository {

    override suspend fun insertCardElement(cardElement: CardElement): Result<Long> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardElement.cardId)
                cardElementDao.insertElement(cardElement.toEntity())
            }
        }

    override suspend fun deleteCardElementsByCardId(cardId: Long): Result<Int> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardId)
                cardElementDao.deleteByCardId(cardId)
            }
        }

    override suspend fun deleteCardElementById(cardId: Long, elementId: Long): Result<Int> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardId)
                cardElementDao.deleteElementById(elementId)
            }
        }

    override suspend fun deleteCardElementsByIds(cardId: Long, elementIds: List<Long>): Result<Int> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardId)
                cardElementDao.deleteElementsByIds(elementIds)
            }
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

    override fun getTextElementsFlowByCardId(cardId: Long): Flow<Result<List<TextElement>>> {
        return cardElementDao.getTextElementsFlowByCardId(cardId)
            .map { entityList ->
                Result.success(entityList.mapNotNull { text ->
                    text.toDomain().textAttributes?.let { attr ->
                        TextElement(
                            elementId = text.elementId,
                            attributes = attr,
                            posX = text.posX,
                            posY = text.posY,
                            posZ = text.posZ
                        )
                    }
                })
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

    override suspend fun getObjectWithAssetKeys(elementId: Long): Result<CardElementWithAssetKeys> =
        ioCatching {
            cardElementDao.getObjectWithAssetKeys(elementId).toDomain()
        }

    override suspend fun updateElementTransform(cardId: Long, elementId: Long, posX: Float, posY: Float, posZ: Float, scale: Int): Result<Int> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardId)
                cardElementDao.updateElementTransform(elementId, posX, posY, posZ, scale)
            }
        }

    override suspend fun updateTextElement(cardId: Long, elementId: Long, textAttributes: TextAttributes, posX: Float, posY: Float, posZ: Float): Result<Int> =
        ioCatching {
            db.withTransaction {
                cardDao.refreshUpdatedAt(cardId)
                cardElementDao.updateTextElement(
                    elementId = elementId,
                    textContent = textAttributes.content,
                    textAlign = textAttributes.alignment.name,
                    textColor = textAttributes.textColor.name,
                    fontSize = textAttributes.fontSize,
                    fontFamily = textAttributes.fontFamily.name,
                    posX = posX,
                    posY = posY,
                    posZ = posZ
                )
            }
        }
}