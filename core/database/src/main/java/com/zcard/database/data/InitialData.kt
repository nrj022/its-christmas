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
            //AssetEntity(assetId = 7, assetType = "OBJECT", unityKey = "m_001", thumbnailKey = "thumb_m_001"),
            AssetEntity(assetId = 8, assetType = "OBJECT", unityKey = "m_002", thumbnailKey = "thumb_m_002"),
            //AssetEntity(assetId = 9, assetType = "OBJECT", unityKey = "m_003", thumbnailKey = "thumb_m_003"),
            AssetEntity(assetId = 10, assetType = "OBJECT", unityKey = "m_004", thumbnailKey = "thumb_m_004"),
            AssetEntity(assetId = 11, assetType = "OBJECT", unityKey = "m_005", thumbnailKey = "thumb_m_005"),
            AssetEntity(assetId = 12, assetType = "OBJECT", unityKey = "m_006", thumbnailKey = "thumb_m_006"),
            AssetEntity(assetId = 13, assetType = "OBJECT", unityKey = "m_007", thumbnailKey = "thumb_m_007"),
            AssetEntity(assetId = 14, assetType = "OBJECT", unityKey = "m_008", thumbnailKey = "thumb_m_008"),
            AssetEntity(assetId = 15, assetType = "OBJECT", unityKey = "m_009", thumbnailKey = "thumb_m_009"),
            AssetEntity(assetId = 16, assetType = "OBJECT", unityKey = "m_010", thumbnailKey = "thumb_m_010"),
            AssetEntity(assetId = 17, assetType = "OBJECT", unityKey = "m_011", thumbnailKey = "thumb_m_011"),
            AssetEntity(assetId = 18, assetType = "OBJECT", unityKey = "m_012", thumbnailKey = "thumb_m_012"),
            AssetEntity(assetId = 19, assetType = "OBJECT", unityKey = "m_013", thumbnailKey = "thumb_m_013"),
            AssetEntity(assetId = 20, assetType = "OBJECT", unityKey = "m_014", thumbnailKey = "thumb_m_014"),
            AssetEntity(assetId = 21, assetType = "OBJECT", unityKey = "m_015", thumbnailKey = "thumb_m_015"),
            AssetEntity(assetId = 22, assetType = "OBJECT", unityKey = "m_016", thumbnailKey = "thumb_m_016"),
            AssetEntity(assetId = 23, assetType = "OBJECT", unityKey = "m_017", thumbnailKey = "thumb_m_017"),
            AssetEntity(assetId = 24, assetType = "OBJECT", unityKey = "m_018", thumbnailKey = "thumb_m_018"),
            AssetEntity(assetId = 25, assetType = "OBJECT", unityKey = "m_019", thumbnailKey = "thumb_m_019"),
            AssetEntity(assetId = 26, assetType = "OBJECT", unityKey = "m_020", thumbnailKey = "thumb_m_020")
        )
    }
}