package com.zcard.main.model

import com.zcard.domain.model.Card

data class CardItem(
    val card: Card,
    val thumbnailKey: String?
)