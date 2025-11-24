package com.example.domain.repository

import com.example.domain.enum.AssetType
import com.example.domain.model.Asset

interface AssetRepository {

    suspend fun getAssetsByType(assetType: AssetType): List<Asset>

    suspend fun getAssetById(assetId: Long): Asset
}