package com.itschristmas.database.data

import com.itschristmas.database.entity.AssetEntity
import com.itschristmas.database.entity.CardEntity

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
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_001", thumbnailKey = "thumb_bg_001", firebaseFileName = "bg_001.env", firebaseToken = "fae08530-9ede-4a53-bdfb-85809579aa8f"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_002", thumbnailKey = "thumb_bg_002", firebaseFileName = "bg_002.env", firebaseToken = "6f2b05f4-395a-44b2-ac34-b0795a6a7dd2"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_003", thumbnailKey = "thumb_bg_003", firebaseFileName = "bg_003.env", firebaseToken = "de6887be-c699-469e-91b3-7743e71f5079"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_004", thumbnailKey = "thumb_bg_004", firebaseFileName = "bg_004.env", firebaseToken = "45fe535f-b78c-4062-a4d4-66d5c61ae4ac"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_005", thumbnailKey = "thumb_bg_005", firebaseFileName = "bg_005.env", firebaseToken = "ca3b6afb-64d8-4132-bff7-a7c115d82144"),
            AssetEntity(assetType = "BACKGROUND", unityKey = "bg_006", thumbnailKey = "thumb_bg_006", firebaseFileName = "bg_006.env", firebaseToken = "6bf80f47-da18-4333-a241-b1a87a4850f2")
        )
    }

    /* 임시 데이터 // TODO: 추후 제거 예정 */
    fun getInitialCard(): CardEntity {
        return CardEntity(
            cardId = 1,
            backgroundAssetId = 1,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}