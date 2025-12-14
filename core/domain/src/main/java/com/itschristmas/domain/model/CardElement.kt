package com.itschristmas.domain.model

import com.itschristmas.domain.enum.ElementType
import kotlin.random.Random

data class CardElement(
    val elementId: Long = 0,

    val cardId: Long,
    val assetId: Long? = null,
    val elementType: ElementType,

    // Transform
    val posX: Float = Random.nextDouble(-2.0, 2.0).toFloat(),
    val posY: Float = Random.nextDouble(-2.0, 2.0).toFloat(),
    val posZ: Float = 0f,

    val rotX: Int = 0,
    val rotY: Int = 0,
    val rotZ: Int = 0,

    val scale: Int = 1,

    // Text fields (nullable)
    val textAttributes: TextAttributes? = null
)