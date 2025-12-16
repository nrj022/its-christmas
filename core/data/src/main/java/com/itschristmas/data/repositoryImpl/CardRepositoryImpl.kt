package com.itschristmas.data.repositoryImpl

import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.mapper.toEntity
import com.itschristmas.data.repositoryImpl.common.ioCatching
import com.itschristmas.database.dao.CardDao
import com.itschristmas.domain.model.Card
import com.itschristmas.domain.model.GlbAndBgFirebase
import com.itschristmas.domain.repository.CardRepository
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override suspend fun insertCard(card: Card): Result<Long> =
        ioCatching {
            cardDao.insertCard(card.toEntity())
        }

    override suspend fun getCardById(cardId: Long): Result<Card> =
        ioCatching {
            cardDao.getCardById(cardId).toDomain()
        }

    override suspend fun getGlbAndBgFirebase(cardId: Long): Result<GlbAndBgFirebase> =
        ioCatching {
            cardDao.getGlbAndBgFirebaseKey(cardId).toDomain()
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