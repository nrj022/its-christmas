package com.itschristmas.domain.model

import com.itschristmas.domain.enum.ElementType
import kotlin.random.Random

data class CardElement(
    val elementId: Long = 0,

    val cardId: Long,
    val assetId: Long? = null,
    val elementType: ElementType,

    // Transform
    val posX: Float = Random.nextInt(-20, 21) / 10f,
    val posY: Float = Random.nextInt(-20, 21) / 10f,
    val posZ: Float = Random.nextInt(-10, 11) / 10f,

    val rotX: Int = 0,
    val rotY: Int = 0,
    val rotZ: Int = 0,

    val scale: Int = 1,

    // Text fields (nullable)
    val textAttributes: TextAttributes? = null
)