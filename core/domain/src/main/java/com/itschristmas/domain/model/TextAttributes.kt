package com.itschristmas.domain.model

data class TextAttributes(
    val content: String = "New Text",
    val fontFamily: FontOption = FontOption.PlaywriteUsTradGuides,
    val fontSize: Float = 14f,
    val textColor: ColorOption = ColorOption.Black,
    val alignment: TextAlignmentOption = TextAlignmentOption.Right,
)