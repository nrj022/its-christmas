package com.example.data.repositoryImpl

import com.example.data.mapper.toDomain
import com.example.database.dao.AssetDao
import com.example.domain.enum.AssetType
import com.example.domain.model.Asset
import com.example.domain.repository.AssetRepository
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao
): AssetRepository {

    override suspend fun getAssetsByType(assetType: AssetType): List<Asset> {
        return assetDao.getByType(assetType.name).map { it.toDomain() }
    }

    override suspend fun getAssetById(assetId: Long): Asset {
        return assetDao.getById(assetId).toDomain()
    }
}