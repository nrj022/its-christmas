package com.zcard.domain.model

data class CardElementWithAssetKeys(
    val cardElement: CardElement,

    // Asset keys
    val unityKey: String,
    val thumbnailKey: String,
)