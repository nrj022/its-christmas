package com.itschristmas.domain.model

data class CardElementWithAssetKeys(
    val cardElement: CardElement,

    // Asset keys
    val unityKey: String,
    val thumbnailKey: String,
)