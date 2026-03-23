package com.zcard.database.dto

import androidx.room.Embedded
import com.zcard.database.entity.CardElementEntity

data class CardElementWithAssetKeysDto(
    @Embedded
    val cardElement: CardElementEntity,

    // Asset keys
    val unityKey: String,
    val thumbnailKey: String,
)