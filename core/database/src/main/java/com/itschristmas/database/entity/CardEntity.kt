package com.itschristmas.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0,

    val title: String,
    val glbFileName: String?,
    val glbToken: String?,
    val backgroundAssetId: Long,
    val isDraft: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
