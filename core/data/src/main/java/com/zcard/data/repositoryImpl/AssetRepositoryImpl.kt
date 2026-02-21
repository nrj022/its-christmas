package com.zcard.data.repositoryImpl

import com.zcard.data.mapper.toDomain
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.dao.AssetDao
import com.zcard.database.data.InitialData
import com.zcard.domain.enum.AssetType
import com.zcard.domain.model.Asset
import com.zcard.domain.repository.AssetRepository
import javax.inject.Inject

class AssetRepositoryImpl @Inject constructor(
    private val assetDao: AssetDao
) : AssetRepository {

    override fun getInitialBackgrounds(): List<Asset> =
        InitialData.getInitialBackgrounds().map { it.toDomain() }

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