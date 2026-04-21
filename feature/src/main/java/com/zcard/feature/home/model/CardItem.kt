package com.zcard.feature.home.model

import com.zcard.domain.model.Card
import java.io.File

data class CardItem(
    val card: Card,
    val thumbnailFile: File? = null,
    val backgroundKey: String? = null
)