package com.zcard.domain.model

data class Card(
    val cardId: Long = 0,
    val exportId: Long = System.currentTimeMillis(),
    val title: String = "New Card",
    val glbFileName: String? = null,
    val backgroundAssetId: Long = 1,
    val isDraft: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailUpdatedAt: Long = 0L,
)