package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val assetId: Long = 0,

    val assetType: AssetType,         // MODEL / DECORATION / BACKGROUND / TEXT_TEMPLATE
    val unityKey: String,             // Addressable key or prefab name
    val thumbnailKey: String         // local path or remote URL
)

enum class AssetType {
    MODEL,
    DECORATION,
    BACKGROUND,
    TEXT_TEMPLATE
}