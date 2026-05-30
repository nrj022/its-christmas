package com.zcard.domain.model

import kotlin.random.Random

data class TextElement(
    val elementId: Long? = null,
    val attributes: TextAttributes = TextAttributes(),
    val posX: Float = Random.nextInt(-20, 21) / 10f,
    val posY: Float = Random.nextInt(-20, 21) / 10f,
    val posZ: Float = Random.nextInt(-10, 11) / 10f
)