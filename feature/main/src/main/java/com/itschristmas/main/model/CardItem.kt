package com.itschristmas.main.model

import com.itschristmas.domain.model.Card

data class CardItem(
    val card: Card,
    val thumbnailKey: String?
)