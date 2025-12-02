package com.itschristmas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.itschristmas.database.dto.GlbAndBgFirebaseKeyDto
import com.itschristmas.database.entity.CardEntity

@Dao
interface CardDao {

    @Insert
    suspend fun insertCard(card: CardEntity): Long

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE cardId = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity

    @Query("""
        SELECT cards.glbKey AS glbKey, assets.unityKey AS backgroundKey 
        FROM cards 
        JOIN assets ON cards.backgroundAssetId = assets.assetId 
        WHERE cards.cardId = :cardId
        """)
    suspend fun getGlbAndBgFirebaseKey(cardId: Long): GlbAndBgFirebaseKeyDto

    @Query("UPDATE cards SET backgroundAssetId = :backgroundAssetId WHERE cardId = :cardId")
    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Int

    @Query("UPDATE cards SET glbKey = :glbKey WHERE cardId = :cardId")
    suspend fun updateGlbKey(cardId: Long, glbKey: String): Int
}