package com.itschristmas.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itschristmas.database.entity.CardElementEntity
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

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'OBJECT'")
    fun getObjectElementsByCardId(cardId: Long): Flow<List<CardElementEntity>>

    @Query("SELECT * FROM card_elements WHERE cardId = :cardId and elementType = 'TEXT'")
    suspend fun getTextElementsByCardId(cardId: Long): List<CardElementEntity>
}