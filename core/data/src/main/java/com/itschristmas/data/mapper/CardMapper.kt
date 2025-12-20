package com.itschristmas.data.mapper

import com.itschristmas.database.entity.CardEntity
import com.itschristmas.domain.model.Card

fun CardEntity.toDomain(): Card = Card(
    cardId = cardId,
    title = title,
    glbFileName = glbFileName,
    glbToken = glbToken,
    backgroundAssetId = backgroundAssetId,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Card.toEntity(): CardEntity = CardEntity(
    cardId = cardId,
    title = title,
    glbFileName = glbFileName,
    glbToken = glbToken,
    backgroundAssetId = backgroundAssetId,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt
)