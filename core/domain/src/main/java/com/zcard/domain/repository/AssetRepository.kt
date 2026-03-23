package com.zcard.domain.repository

import com.zcard.domain.model.Asset

interface AssetRepository {

    fun getInitialBackgrounds(): List<Asset>

    suspend fun getObjects(): Result<List<Asset>>

    suspend fun getBackgrounds(): Result<List<Asset>>

    suspend fun getAssetById(assetId: Long): Result<Asset>

    suspend fun getUnityKeyById(assetId: Long): Result<String>
}