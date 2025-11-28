package com.itschristmas.data.repositoryImpl

import com.itschristmas.data.mapper.toDomain
import com.itschristmas.database.dao.AssetDao
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.repository.AssetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao
) : AssetRepository {

    override suspend fun getAssetsByType(assetType: AssetType): List<Asset> {
        return withContext(Dispatchers.IO) {
            assetDao.getByType(assetType.name).map { it.toDomain() }
        }
    }

    override suspend fun getAssetById(assetId: Long): Asset {
        return withContext(Dispatchers.IO) {
            assetDao.getById(assetId).toDomain()
        }
    }
}