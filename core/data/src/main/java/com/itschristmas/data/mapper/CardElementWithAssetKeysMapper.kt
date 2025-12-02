package com.itschristmas.data.mapper

import com.itschristmas.database.dto.CardElementWithAssetKeysDto
import com.itschristmas.domain.model.CardElementWithAssetKeys


fun CardElementWithAssetKeysDto.toDomain(): CardElementWithAssetKeys {
    return CardElementWithAssetKeys(
        cardElement = cardElement.toDomain(),
        unityKey = unityKey,
        thumbnailKey = thumbnailKey
    )
}