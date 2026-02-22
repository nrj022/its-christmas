package com.zcard.database.data

import com.zcard.database.entity.AssetEntity

object InitialData {

    fun getInitialBackgrounds(): List<AssetEntity> {
        return listOf(
            // 배경
            AssetEntity(assetId = 1, assetType = "BACKGROUND", unityKey = "bg_001", thumbnailKey = "thumb_bg_001"),
            AssetEntity(assetId = 2, assetType = "BACKGROUND", unityKey = "bg_002", thumbnailKey = "thumb_bg_002"),
            AssetEntity(assetId = 3, assetType = "BACKGROUND", unityKey = "bg_003", thumbnailKey = "thumb_bg_003"),
            AssetEntity(assetId = 4, assetType = "BACKGROUND", unityKey = "bg_004", thumbnailKey = "thumb_bg_004"),
            AssetEntity(assetId = 5, assetType = "BACKGROUND", unityKey = "bg_005", thumbnailKey = "thumb_bg_005"),
            AssetEntity(assetId = 6, assetType = "BACKGROUND", unityKey = "bg_006", thumbnailKey = "thumb_bg_006")
        )
    }

    fun getInitialAssets(): List<AssetEntity> {
        return listOf(
            // 모델
            AssetEntity(assetId = 7, assetType = "OBJECT", unityKey = "m_001", thumbnailKey = "thumb_m_001"),
            AssetEntity(assetId = 8, assetType = "OBJECT", unityKey = "m_002", thumbnailKey = "thumb_m_002")
        )
    }
}