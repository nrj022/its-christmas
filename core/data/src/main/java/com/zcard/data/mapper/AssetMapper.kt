package com.zcard.data.mapper

import com.zcard.database.entity.AssetEntity
import com.zcard.domain.enum.AssetType
import com.zcard.domain.model.Asset

fun AssetEntity.toDomain(): Asset {
    return Asset(
        assetId = assetId,
        assetType = AssetType.valueOf(assetType),
        unityKey = unityKey,
        thumbnailKey = thumbnailKey,
    )
}