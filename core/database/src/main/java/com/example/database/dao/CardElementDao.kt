package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.CardElementEntity

@Dao
interface CardElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cardElements: List<CardElementEntity>): List<Long>

    @Query("DELETE FROM card_elements")
    suspend fun deleteAll()

    @Query("DELETE FROM card_elements WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: Long)

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'ASSET'")
    suspend fun getAssetElementsByCardId(cardId: Long): List<CardElementEntity>

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'TEXT'")
    suspend fun getTextElementsByCardId(cardId: Long): List<CardElementEntity>
}