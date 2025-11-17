package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.AssetEntity

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(assets: List<AssetEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(asset: AssetEntity): Long

    @Query("SELECT * FROM assets")
    suspend fun getAll(): List<AssetEntity>
}