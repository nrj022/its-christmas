package com.zcard.domain.repository

import com.zcard.domain.enum.AssetType
import com.zcard.domain.model.Asset

interface AssetRepository {

    fun getInitialBackgrounds(): List<Asset>

    suspend fun getAssetsByType(assetType: AssetType): Result<List<Asset>>

    suspend fun getAssetById(assetId: Long): Result<Asset>

    suspend fun getUnityKeyById(assetId: Long): Result<String>
}