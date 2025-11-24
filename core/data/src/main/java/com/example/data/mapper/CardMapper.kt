package com.example.data.mapper

import com.example.database.entity.CardEntity
import com.example.domain.model.Card

fun CardEntity.toDomain(): Card = Card(
    cardId = cardId,
    title = title,
    glbKey = glbKey,
    thumbnailPath = thumbnailPath,
    backgroundAssetId = backgroundAssetId,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Card.toEntity(): CardEntity = CardEntity(
    cardId = cardId,
    title = title,
    glbKey = glbKey,
    thumbnailPath = thumbnailPath,
    backgroundAssetId = backgroundAssetId,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt
)