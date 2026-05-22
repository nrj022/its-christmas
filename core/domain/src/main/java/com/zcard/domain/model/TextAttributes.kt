package com.zcard.domain.model

data class TextAttributes(
    val content: String = "New Text",
    val fontFamily: TextFontFamily = TextFontFamily.PlaywriteUsTradGuides,
    val fontSize: Float = 14f,
    val textColor: TextColor = TextColor.Black,
    val alignment: TextAlignment = TextAlignment.Right,
)