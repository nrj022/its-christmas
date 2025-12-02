package com.itschristmas.data.repositoryImpl

import com.itschristmas.data.mapper.toDomain
import com.itschristmas.data.repositoryImpl.common.ioCatching
import com.itschristmas.database.dao.AssetDao
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.repository.AssetRepository
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao
) : AssetRepository {

    override suspend fun getAssetsByType(assetType: AssetType): Result<List<Asset>> =
        ioCatching { assetDao.getByType(assetType.name).map { it.toDomain() } }

    override suspend fun getAssetById(assetId: Long): Result<Asset> =
        ioCatching { assetDao.getById(assetId).toDomain() }

    override suspend fun getUnityKeyById(assetId: Long): Result<String> =
        ioCatching {
            val unityKey = assetDao.getUnityKeyById(assetId)
            if(unityKey.isEmpty()) throw IllegalStateException("Invalid unityKey")
            unityKey
        }
}