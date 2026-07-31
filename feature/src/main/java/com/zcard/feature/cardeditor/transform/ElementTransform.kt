package com.zcard.feature.cardeditor.transform

data class ElementTransform(
    val posX: Float = 0f,
    val posY: Float = 0f,
    val posZ: Float = 0f,
    val rotation: Quaternion = Quaternion.identity,
    val scale: Int = 1
)