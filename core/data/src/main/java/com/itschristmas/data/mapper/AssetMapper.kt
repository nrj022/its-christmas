package com.itschristmas.data.mapper

import com.itschristmas.database.entity.AssetEntity
import com.itschristmas.domain.enum.AssetType
import com.itschristmas.domain.model.Asset

fun AssetEntity.toDomain(): Asset {
    return Asset(
        assetId = assetId,
        assetType = AssetType.valueOf(assetType),
        firebaseFileName = firebaseFileName,
        firebaseToken = firebaseToken,
        unityKey = unityKey,
        thumbnailKey = thumbnailKey,
    )
}