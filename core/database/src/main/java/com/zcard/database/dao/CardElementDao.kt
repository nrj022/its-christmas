package com.zcard.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zcard.database.entity.CardElementEntity
import com.zcard.database.dto.CardElementWithAssetKeysDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CardElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(cardElement: CardElementEntity): Long

    @Query("DELETE FROM card_elements")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM card_elements WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: Long): Int

    @Query("DELETE FROM card_elements WHERE elementId = :elementId")
    suspend fun deleteElementById(elementId: Long): Int

    @Query("DELETE FROM card_elements WHERE elementId IN (:elementIds)")
    suspend fun deleteElementsByIds(elementIds: List<Long>): Int

    @Query("""
        SELECT ce.*, a.thumbnailKey, a.unityKey
        FROM card_elements AS ce
        JOIN assets AS a ON ce.assetId = a.assetId
        WHERE ce.cardId = :cardId 
        and elementType = 'OBJECT'
        ORDER BY ce.elementId DESC
        """)
    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<List<CardElementWithAssetKeysDto>>

    @Query("""
        SELECT * FROM card_elements 
        WHERE cardId = :cardId 
        and elementType = 'TEXT' 
        ORDER BY elementId DESC
        """)
    suspend fun getTextElementsByCardId(cardId: Long): List<CardElementEntity>

    @Query("""
        SELECT ce.*, a.thumbnailKey, a.unityKey
        FROM card_elements AS ce
        JOIN assets AS a ON ce.assetId = a.assetId
        WHERE ce.elementId = :elementId 
        and elementType = 'OBJECT'
        """)
    suspend fun getObjectWithAssetKeys(elementId: Long): CardElementWithAssetKeysDto

    @Query("UPDATE card_elements SET posX = :posX, posY = :posY, posZ = :posZ, scale = :scale WHERE elementId = :elementId")
    suspend fun updateElementTransform(elementId: Long, posX: Float, posY: Float, posZ: Float, scale: Int): Int

    @Query("""
        UPDATE card_elements 
        SET textContent = :textContent, textAlign = :textAlign, textColor = :textColor, fontSize = :fontSize, fontFamily = :fontFamily, posX = :posX, posY = :posY, posZ = :posZ
        WHERE elementId = :elementId
        """)
    suspend fun updateTextElement(elementId: Long, textContent: String, textAlign: String, textColor: String, fontSize: Float, fontFamily: String, posX: Float, posY: Float, posZ: Float): Int
}