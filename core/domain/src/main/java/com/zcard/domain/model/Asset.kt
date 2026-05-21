package com.zcard.domain.model

data class Asset(
    val assetId: Long = 0,
    val assetType: AssetType,
    val unityKey: String,
    val thumbnailKey: String
)
