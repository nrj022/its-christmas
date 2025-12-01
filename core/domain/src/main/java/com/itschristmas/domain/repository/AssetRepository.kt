package com.itschristmas.domain.repository

import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.model.Asset

interface AssetRepository {

    suspend fun getAssetsByType(assetType: AssetType): List<Asset>

    suspend fun getAssetById(assetId: Long): Asset

    suspend fun getUnityKeyById(assetId: Long): String?
}