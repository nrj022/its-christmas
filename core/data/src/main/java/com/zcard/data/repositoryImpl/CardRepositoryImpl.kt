package com.zcard.data.repositoryImpl

import com.zcard.data.mapper.toDomain
import com.zcard.data.mapper.toEntity
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.dao.CardDao
import com.zcard.domain.model.Card
import com.zcard.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override suspend fun insertCard(card: Card): Result<Long> =
        ioCatching {
            cardDao.insertCard(card.toEntity())
        }

    override suspend fun getAllCards(): Result<List<Card>> =
        ioCatching {
            cardDao.getAllCards().map { it.toDomain() }
        }

    override suspend fun getCardById(cardId: Long): Result<Card> =
        ioCatching {
            cardDao.getCardById(cardId).toDomain()
        }

    override suspend fun updateCardTitle(cardId: Long, title: String): Result<Int> =
        ioCatching {
            cardDao.updateTitle(cardId, title)
        }

    override suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int> =
        ioCatching {
            cardDao.updateBackgroundAssetId(cardId, backgroundAssetId)
        }

    override suspend fun updateGlb(cardId: Long, glbFileName: String, glbToken: String): Result<Int> =
        ioCatching {
            cardDao.updateGlb(cardId, glbFileName, glbToken)
        }
}