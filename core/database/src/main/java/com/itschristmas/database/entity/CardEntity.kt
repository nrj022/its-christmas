package com.itschristmas.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,

    val title: String? = null,
    val glbKey: String? = null,
    val thumbnailPath: String? = null,
    val backgroundAssetId: Long = 1,    // 기본값
    val isDraft: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
