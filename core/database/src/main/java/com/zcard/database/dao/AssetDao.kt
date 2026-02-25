package com.zcard.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zcard.database.entity.AssetEntity

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<AssetEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Query("SELECT * FROM assets WHERE assetType = :assetType ORDER BY assetId DESC")
    suspend fun getByType(assetType: String): List<AssetEntity>

    @Query("SELECT * FROM assets WHERE assetId = :assetId")
    suspend fun getById(assetId: Long): AssetEntity

    @Query("SELECT unityKey FROM assets WHERE assetId = :assetId")
    suspend fun getUnityKeyById(assetId: Long): String
}