package com.itschristmas.card.cardeditor.model

import com.itschristmas.domain.model.TextElement

data class TempTextElement(
    val tempId: Long = System.nanoTime(),
    val textElement: TextElement = TextElement()
)