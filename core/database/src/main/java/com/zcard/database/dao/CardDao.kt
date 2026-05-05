package com.zcard.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zcard.database.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Insert
    suspend fun insertCard(card: CardEntity): Long

    @Query("SELECT * FROM cards ORDER BY updatedAt DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE cardId = :cardId")
    suspend fun getCardById(cardId: Long): CardEntity

    @Query("UPDATE cards SET updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun refreshUpdatedAt(cardId: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET thumbnailUpdatedAt = :thumbnailUpdatedAt WHERE cardId = :cardId")
    suspend fun refreshThumbnailUpdatedAt(cardId: Long, thumbnailUpdatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET title = :title, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun updateTitle(cardId: Long, title: String, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET backgroundAssetId = :backgroundAssetId, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long, updatedAt: Long = System.currentTimeMillis()): Int

    @Query("UPDATE cards SET glbFileName = :glbFileName, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun updateGlb(cardId: Long, glbFileName: String, updatedAt: Long = System.currentTimeMillis()): Int
}