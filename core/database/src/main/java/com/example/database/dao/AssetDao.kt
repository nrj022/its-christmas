package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.AssetEntity
import com.example.database.entity.AssetType

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(assets: List<AssetEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Query("SELECT * FROM assets")
    suspend fun getAll(): List<AssetEntity>

    @Query("SELECT * FROM assets WHERE assetType = :assetType")
    suspend fun getByType(assetType: AssetType): List<AssetEntity>

    @Query("SELECT * FROM assets WHERE assetId = :assetId")
    suspend fun getById(assetId: Long): AssetEntity
}