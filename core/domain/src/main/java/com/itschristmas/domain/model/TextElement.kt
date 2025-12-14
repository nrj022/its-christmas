package com.itschristmas.domain.model

import kotlin.random.Random

data class TextElement(
    val elementId: Long? = null,
    val attributes: TextAttributes = TextAttributes(
        content = "New Text",
        alignment = TextAlignmentOption.Right,
        textColor = ColorOption.Black,
        fontSize = 14f,
        fontFamily = FontOption.PlaywriteUsTradGuides,
    ),
    val posX: Float = Random.nextDouble(-2.0, 2.0).toFloat(),
    val posY: Float = Random.nextDouble(-2.0, 2.0).toFloat(),
    val posZ: Float = 0f
)