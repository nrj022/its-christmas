package com.itschristmas.database.entity

import androidx.room.Embedded

data class CardElementWithAssetKeysEntity(
    @Embedded
    val cardElement: CardElementEntity,

    // Asset keys
    val unityKey: String,
    val thumbnailKey: String,
)