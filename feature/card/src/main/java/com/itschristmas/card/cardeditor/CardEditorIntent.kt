package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElementWithAssetKeys

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
    data class MyObjectClicked(val element: CardElementWithAssetKeys): CardEditorIntent()
    data object DeleteClicked: CardEditorIntent()
    data object AdjustClicked: CardEditorIntent()
    data object AdjustCancelClicked: CardEditorIntent()
    data object AdjustApplyClicked: CardEditorIntent()
    data class DirectionalClicked(val direction: Direction): CardEditorIntent()
    data class ScaleChanged(val newScale: Int): CardEditorIntent()
    data object CameraResetClicked: CardEditorIntent()
    data object TransformResetClicked: CardEditorIntent()
}