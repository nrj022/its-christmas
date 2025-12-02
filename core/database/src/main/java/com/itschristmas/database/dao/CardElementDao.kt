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

    @Query("UPDATE card_elements SET posX = :posX, posY = :posY WHERE elementId = :elementId")
    suspend fun updateElementPosition(elementId: Long, posX: Int, posY: Int): Int

    @Query("UPDATE card_elements SET scale = :scale WHERE elementId = :elementId")
    suspend fun updateElementScale(elementId: Long, scale: Int): Int

    @Query("UPDATE card_elements SET fontSize = :fontSize WHERE elementId = :elementId and elementType = 'TEXT'")
    suspend fun updateTextFontSize(elementId: Long, fontSize: Float): Int

    @Query("UPDATE card_elements SET textColor = :textColor WHERE elementId = :elementId and elementType = 'TEXT'")
    suspend fun updateTextColor(elementId: Long, textColor: String): Int

    @Query("UPDATE card_elements SET textContent = :textContent WHERE elementId = :elementId and elementType = 'TEXT'")
    suspend fun updateTextContent(elementId: Long, textContent: String): Int

    @Query("UPDATE card_elements SET fontFamily = :fontFamily WHERE elementId = :elementId and elementType = 'TEXT'")
    suspend fun updateTextFont(elementId: Long, fontFamily: String): Int

    @Query("UPDATE card_elements SET textAlign = :textAlign WHERE elementId = :elementId and elementType = 'TEXT'")
    suspend fun updateTextAlign(elementId: Long, textAlign: String): Int
}