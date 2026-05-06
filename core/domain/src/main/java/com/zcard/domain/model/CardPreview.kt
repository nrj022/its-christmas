package com.zcard.domain.model

import java.io.File

data class CardPreview(
    val cardId: Long,
    val title: String,
    val updatedAt: Long,
    val thumbnailFile: File? = null,
    val thumbnailUpdatedAt: Long = 0L,
)