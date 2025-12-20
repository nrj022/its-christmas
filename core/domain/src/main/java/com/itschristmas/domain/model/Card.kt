package com.itschristmas.domain.model

data class Card(
    val cardId: Long = 0,
    val title: String = "New Card",
    val glbFileName: String? = null,
    val glbToken: String? = null,
    val backgroundAssetId: Long = -1,
    val isDraft: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)