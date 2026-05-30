package com.zcard.domain.model

data class TextAttributes(
    val content: String = "New Text",
    val fontFamily: TextFontFamily = TextFontFamily.entries.first(),
    val fontSize: Float = 8f,
    val textColor: TextColor = TextColor.Black,
    val alignment: TextAlignment = TextAlignment.Right,
)