package com.zcard.feature.home.model

import com.zcard.domain.model.Card

data class CardItem(
    val card: Card,
    val thumbnailKey: String?
)