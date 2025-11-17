package com.example.database.data

import com.example.database.entity.AssetEntity
import com.example.database.entity.AssetType

object InitialData {
    fun getInitialAssets(): List<AssetEntity> {
        return listOf(
            // 모델
            AssetEntity(assetType = AssetType.MODEL, unityKey = "m_001", thumbnailKey = "thumb_m_001"),
            AssetEntity(assetType = AssetType.MODEL, unityKey = "m_002", thumbnailKey = "thumb_m_002")
        )
    }
}