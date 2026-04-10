package com.zcard.feature.cardeditor.model

import com.zcard.domain.model.TextElement

data class TempTextElement(
    val tempId: Long = System.nanoTime(),
    val textElement: TextElement = TextElement()
)