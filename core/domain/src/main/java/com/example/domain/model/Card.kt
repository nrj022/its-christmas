package com.example.domain.model

data class Card(
    val cardId: Long = 0,
    val title: String? = null,
    val glbKey: String? = null,
    val thumbnailPath: String? = null,
    val backgroundAssetId: Long? = null,
    val isDraft: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)