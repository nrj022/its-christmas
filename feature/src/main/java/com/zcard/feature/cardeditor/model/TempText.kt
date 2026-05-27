package com.zcard.feature.cardeditor.model

import com.zcard.domain.model.TextElement

data class TempText(
    val tempId: Long = System.nanoTime(),
    val textElement: TextElement = TextElement()
)