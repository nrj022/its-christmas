package com.zcard.data.mapper

import com.zcard.database.dto.CardElementWithAssetKeysDto
import com.zcard.domain.model.CardElementWithAssetKeys


fun CardElementWithAssetKeysDto.toDomain(): CardElementWithAssetKeys {
    return CardElementWithAssetKeys(
        cardElement = cardElement.toDomain(),
        unityKey = unityKey,
        thumbnailKey = thumbnailKey
    )
}