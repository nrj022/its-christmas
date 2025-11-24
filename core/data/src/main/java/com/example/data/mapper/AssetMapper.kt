package com.example.data.mapper

import com.example.database.entity.AssetEntity
import com.example.domain.enum.AssetType
import com.example.domain.model.Asset

fun AssetEntity.toDomain(): Asset {
    return Asset(
        assetId = assetId,
        assetType = AssetType.valueOf(assetType),
        firebaseKey = firebaseKey,
        unityKey = unityKey,
        thumbnailKey = thumbnailKey,
    )
}