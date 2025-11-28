package com.itschristmas.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val assetId: Long = 0,

    val assetType: String,         // AssetType: OBJECT / DECORATION / BACKGROUND
    val firebaseKey: String? = null,  // null when assetType = OBJECT / DECORATION
    val unityKey: String,             // Addressable key or prefab name
    val thumbnailKey: String         // addressable key
)