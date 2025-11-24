package com.example.domain.model

import com.example.domain.enum.ElementType

data class CardElement(
    val elementId: Long = 0,

    val cardId: Long,
    val assetId: Long? = null,
    val elementType: ElementType,

    // Transform
    val posX: Int,
    val posY: Int,
    val posZ: Int? = null,

    val rotX: Int? = null,
    val rotY: Int? = null,
    val rotZ: Int? = null,

    val scale: Int,

    // Text fields (nullable)
    val unityTextStyle: UnityTextStyle? = null
)