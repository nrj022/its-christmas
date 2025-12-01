package com.itschristmas.data.repositoryImpl

import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.mapper.toEntity
import com.itschristmas.database.dao.CardDao
import com.itschristmas.domain.model.Card
import com.itschristmas.domain.repository.CardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override suspend fun insertCard(card: Card): Long {
        return withContext(Dispatchers.IO) {
            cardDao.insertCard(card.toEntity())
        }
    }

    override suspend fun getCardById(cardId: Long): Card {
        return withContext(Dispatchers.IO) {
            cardDao.getCardById(cardId).toDomain()
        }
    }

    override suspend fun getGlbKeyAndBackgroundUnityKey(cardId: Long): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            cardDao.getGlbKeyAndBackgroundUnityKey(cardId)
        }
    }

    override suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Int {
        return withContext(Dispatchers.IO) {
            cardDao.updateBackgroundAssetId(cardId, backgroundAssetId)
        }
    }

    override suspend fun updateGlbKey(cardId: Long, glbKey: String): Int {
        return withContext(Dispatchers.IO) {
            cardDao.updateGlbKey(cardId, glbKey)
        }
    }
}