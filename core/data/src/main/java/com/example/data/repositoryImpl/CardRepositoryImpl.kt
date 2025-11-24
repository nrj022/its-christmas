package com.example.data.repositoryImpl

import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.database.dao.CardDao
import com.example.domain.model.Card
import com.example.domain.repository.CardRepository
import java.io.File
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
): CardRepository {

    override suspend fun insertCard(card: Card): Long {
        return cardDao.insertCard(card.toEntity())
    }

    override suspend fun getCardById(cardId: Long): Card {
        return cardDao.getCardById(cardId).toDomain()
    }

    override suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Int) {
        cardDao.updateBackgroundAssetId(cardId, backgroundAssetId)
    }

    // TODO: Firebase Storage GLB 파일 업로드 기능 구현
    override suspend fun uploadGlbFileToFirebase(glbFile: File): String {
        return ""
    }
}