package com.itschristmas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itschristmas.database.entity.CardElementEntity
import com.itschristmas.database.entity.CardElementWithAssetKeysEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cardElements: List<CardElementEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElement(cardElement: CardElementEntity): Long

    @Query("DELETE FROM card_elements")
    suspend fun deleteAll()

    @Query("DELETE FROM card_elements WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: Long)

    @Query("""
        SELECT ce.*, a.thumbnailKey, a.unityKey
        FROM card_elements AS ce
        JOIN assets AS a ON ce.assetId = a.assetId
        WHERE ce.cardId = :cardId 
        and elementType = 'OBJECT'
        """)
    fun getObjectElementsWithAssetKeysByCardId(cardId: Long): Flow<List<CardElementWithAssetKeysEntity>>

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'TEXT'")
    suspend fun getTextElementsByCardId(cardId: Long): List<CardElementEntity>
}