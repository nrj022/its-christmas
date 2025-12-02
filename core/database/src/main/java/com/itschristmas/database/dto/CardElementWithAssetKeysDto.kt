package com.itschristmas.database.dto

import androidx.room.Embedded
import com.itschristmas.database.entity.CardElementEntity

data class CardElementWithAssetKeysDto(
    @Embedded
    val cardElement: CardElementEntity,

    // Asset keys
    val unityKey: String,
    val thumbnailKey: String,
)