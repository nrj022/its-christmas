package com.zcard.domain.model

import com.zcard.domain.enum.AssetType

data class Asset(
    val assetId: Long = 0,
    val assetType: AssetType,
    val firebaseFileName: String? = null,    // null when assetType = OBJECT / DECORATION
    val firebaseToken: String? = null,    // null when assetType = OBJECT / DECORATION
    val unityKey: String,
    val thumbnailKey: String
)
