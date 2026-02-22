package com.zcard.data.mapper

import com.zcard.database.entity.CardEntity
import com.zcard.domain.model.Card

fun CardEntity.toDomain(): Card = Card(
    cardId = cardId,
    exportId = exportId,
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
    exportId = exportId,
    title = title,
    glbFileName = glbFileName,
    glbToken = glbToken,
    backgroundAssetId = backgroundAssetId,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt
)