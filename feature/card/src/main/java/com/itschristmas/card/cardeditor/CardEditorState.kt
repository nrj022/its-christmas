package com.itschristmas.card.cardeditor

import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.Asset

data class CardEditorState(
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val myObjects: List<CardElementWithAssetKeys> = emptyList(),
    val selectedBackground: Long? = null,
    val selectedMyObject: Long? = null
)