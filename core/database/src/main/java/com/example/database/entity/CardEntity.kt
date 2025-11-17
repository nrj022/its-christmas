package com.example.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,

    val title: String? = null,
    val glbKey: String? = null,
    val thumbnailPath: String? = null,
    val backgroundAssetId: Int? = null,   // FK: Asset.assetId
    val isDraft: Boolean = true,
    val syncState: Int = 0, // 0: local-only, 1: needs upload, 2: synced
    val createdAt: Long,
    val updatedAt: Long
)
