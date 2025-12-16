package com.itschristmas.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,

    val title: String? = null,
    val glbFileName: String? = null,
    val glbToken: String? = null,
    val thumbnailPath: String? = null,
    val backgroundAssetId: Long,
    val isDraft: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
