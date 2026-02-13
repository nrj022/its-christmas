package com.itschristmas.domain.repository

import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.model.Asset

interface AssetRepository {

    fun getInitialBackgrounds(): List<Asset>

    suspend fun getAssetsByType(assetType: AssetType): Result<List<Asset>>

    suspend fun getAssetById(assetId: Long): Result<Asset>

    suspend fun getUnityKeyById(assetId: Long): Result<String>
}