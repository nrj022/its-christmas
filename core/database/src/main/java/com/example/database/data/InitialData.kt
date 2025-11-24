package com.example.database.data

import com.example.database.entity.AssetEntity

object InitialData {
    fun getInitialAssets(): List<AssetEntity> {
        return listOf(
            // 모델
            AssetEntity(assetType = "OBJECT", unityKey = "m_001", thumbnailKey = "thumb_m_001"),
            AssetEntity(assetType = "OBJECT", unityKey = "m_002", thumbnailKey = "thumb_m_002")
        )
    }

    fun getInitialBackgrounds(): List<AssetEntity> {
        return listOf(
            // 배경
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_001", thumbnailKey = "thumb_bg_001"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_002", thumbnailKey = "thumb_bg_002"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_003", thumbnailKey = "thumb_bg_003"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_004", thumbnailKey = "thumb_bg_004"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_005", thumbnailKey = "thumb_bg_005"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_006", thumbnailKey = "thumb_bg_006")
        )
    }
}