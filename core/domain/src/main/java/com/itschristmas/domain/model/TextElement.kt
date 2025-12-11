package com.itschristmas.domain.model

data class TextElement(
    val elementId: Long? = null,
    val attributes: TextAttributes = TextAttributes(
        content = "New Text",
        alignment = TextAlignmentOption.Right,
        textColor = ColorOption.Black,
        fontSize = 14f,
        fontFamily = FontOption.PlaywriteUsTradGuides,
    ),
    val posX: Int = 0,
    val posY: Int = 0
)