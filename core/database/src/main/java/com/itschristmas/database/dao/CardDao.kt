package com.itschristmas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.itschristmas.database.dto.GlbAndBgFirebaseDto
import com.itschristmas.database.entity.CardEntity

@Dao
interface CardDao {

    @Insert
    suspend fun insertCard(card: CardEntity): Long

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE cardId = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity

    @Query(
        """
        SELECT cards.glbFileName AS glbFileName, cards.glbToken AS glbToken, assets.firebaseFileName AS backgroundFileName, assets.firebaseToken AS backgroundToken
        FROM cards 
        JOIN assets ON cards.backgroundAssetId = assets.assetId 
        WHERE cards.cardId = :cardId
        """
    )
    suspend fun getGlbAndBgFirebaseKey(cardId: Long): GlbAndBgFirebaseDto

    @Query("UPDATE cards SET updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun refreshUpdatedAt(cardId: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET backgroundAssetId = :backgroundAssetId, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET glbFileName = :glbFileName, glbToken = :glbToken WHERE cardId = :cardId")
    suspend fun updateGlb(cardId: Long, glbFileName: String, glbToken: String): Int
}