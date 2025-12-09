package com.itschristmas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.database.dto.CardElementWithAssetKeysDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CardElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cardElements: List<CardElementEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(cardElement: CardElementEntity): Long

    @Query("DELETE FROM card_elements")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM card_elements WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: Long): Int

    @Query("DELETE FROM card_elements WHERE elementId = :elementId")
    suspend fun deleteElementById(elementId: Long): Int

    @Query("""
        SELECT ce.*, a.thumbnailKey, a.unityKey
        FROM card_elements AS ce
        JOIN assets AS a ON ce.assetId = a.assetId
        WHERE ce.cardId = :cardId 
        and elementType = 'OBJECT'
        """)
    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<List<CardElementWithAssetKeysDto>>

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'TEXT'")
    suspend fun getTextElementsByCardId(cardId: Long): List<CardElementEntity>

    @Query("UPDATE card_elements SET posX = :posX, posY = :posY, scale = :scale WHERE elementId = :elementId")
    suspend fun updateElementTransform(elementId: Long, posX: Int, posY: Int, scale: Int): Int

    @Query("""
        UPDATE card_elements 
        SET textContent = :textContent, textAlign = :textAlign, textColor = :textColor, fontSize = :fontSize, fontFamily = :fontFamily, posX = :posX, posY = :posY 
        WHERE elementId = :elementId
        """)
    suspend fun updateTextElement(elementId: Long, textContent: String, textAlign: String, textColor: String, fontSize: Float, fontFamily: String, posX: Int, posY: Int): Int
}