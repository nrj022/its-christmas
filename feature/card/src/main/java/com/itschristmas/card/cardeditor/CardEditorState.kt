package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.PanelState
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.Asset

data class CardEditorState(
    val panelState: PanelState = PanelState.ASSET_BROWSER,
    val objects: List<Asset> = emptyList(),
    val backgrounds: List<Asset> = emptyList(),
    val myObjects: List<CardElementWithAssetKeys> = emptyList(),
    val selectedBackground: Long? = null,
    val selectedMyObject: Long? = null
) {
    val showObjectOptionContainer: Boolean
        get() = selectedMyObject != null
}