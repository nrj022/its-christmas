package com.itschristmas.card.cardeditor.model

import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption
import com.itschristmas.domain.model.TextAttributes

data class TempTextElementState(
    val tempId: Long = System.nanoTime(),
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