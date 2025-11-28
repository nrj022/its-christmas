package com.itschristmas.card.ui.cardeditor

import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElement

data class CardEditorState(
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val myObjects: List<CardElement> = emptyList(),
    val assetThumbMap: Map<Long, String> = emptyMap(),
    val selectedBackground: Long? = null,
    val selectedMyObject: Long? = null
)