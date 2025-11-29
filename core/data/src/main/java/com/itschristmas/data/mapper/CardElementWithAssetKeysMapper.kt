package com.itschristmas.data.mapper

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.database.entity.CardElementWithAssetKeysEntity

fun CardElementWithAssetKeysEntity.toDomain(): CardElementWithAssetKeys {
    return CardElementWithAssetKeys(
        cardElement = cardElement.toDomain(),
        unityKey = unityKey,
        thumbnailKey = thumbnailKey
    )
}