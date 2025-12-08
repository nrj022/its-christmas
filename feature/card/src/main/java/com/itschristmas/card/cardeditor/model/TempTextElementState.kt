package com.itschristmas.card.cardeditor.model

import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption

data class TempTextElementState(
    val tempId: Long = System.currentTimeMillis(),
    val elementId: Long? = null,
    val text: String = "New Text",
    val alignment: TextAlignmentOption = TextAlignmentOption.Start,
    val color: ColorOption = ColorOption.Black,
    val fontSize: Float = 14f,
    val fontFamily: FontOption = FontOption.PlaywriteUsTradGuides,
    val posX: Int = 0,
    val posY: Int = 0
)