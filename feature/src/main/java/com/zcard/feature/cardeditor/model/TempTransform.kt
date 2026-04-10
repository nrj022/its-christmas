package com.zcard.feature.cardeditor.model

data class TempTransform(
    val elementId: Long,
    val thumbnailKey: String,
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val scale: Int
)