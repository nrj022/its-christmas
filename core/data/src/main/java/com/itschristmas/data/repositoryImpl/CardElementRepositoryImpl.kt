package com.itschristmas.data.repositoryImpl

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.mapper.toEntity
import com.itschristmas.data.repositoryImpl.common.ioCatching
import com.itschristmas.database.dao.CardElementDao
import com.itschristmas.domain.model.CardElement
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

    override suspend fun deleteCardElementsById(elementId: Long): Result<Int> =
        ioCatching {
            cardElementDao.deleteElementById(elementId)
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

    override suspend fun updateElementTransform(elementId: Long, posX: Int, posY: Int, scale: Int): Result<Int> =
        ioCatching {
            cardElementDao.updateElementTransform(elementId, posX, posY, scale)
        }

    override suspend fun updateTextFontSize(elementId: Long, fontSize: Float): Result<Int> =
        ioCatching {
            cardElementDao.updateTextFontSize(elementId, fontSize)
        }

    override suspend fun updateTextColor(elementId: Long, textColor: String): Result<Int> =
        ioCatching {
            cardElementDao.updateTextColor(elementId, textColor)
        }

    override suspend fun updateTextContent(elementId: Long, textContent: String): Result<Int> =
        ioCatching {
            cardElementDao.updateTextContent(elementId, textContent)
        }

    override suspend fun updateTextFont(elementId: Long, fontFamily: String): Result<Int> =
        ioCatching {
            cardElementDao.updateTextFont(elementId, fontFamily)
        }

    override suspend fun updateTextAlign(elementId: Long, textAlign: String): Result<Int> =
        ioCatching {
            cardElementDao.updateTextAlign(elementId, textAlign)
        }
}