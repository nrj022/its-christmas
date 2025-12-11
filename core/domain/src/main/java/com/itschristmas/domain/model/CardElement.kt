package com.itschristmas.domain.model

import com.itschristmas.domain.enum.ElementType

data class CardElement(
    val elementId: Long = 0,

    val cardId: Long,
    val assetId: Long? = null,
    val elementType: ElementType,

    // Transform
    val posX: Int,
    val posY: Int,
    val posZ: Int = 0,

    val rotX: Int = 0,
    val rotY: Int = 0,
    val rotZ: Int = 0,

    val scale: Int = 1,

    // Text fields (nullable)
    val textAttributes: TextAttributes? = null
)