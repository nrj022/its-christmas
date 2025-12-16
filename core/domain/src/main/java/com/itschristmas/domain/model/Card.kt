package com.itschristmas.domain.model

data class Card(
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