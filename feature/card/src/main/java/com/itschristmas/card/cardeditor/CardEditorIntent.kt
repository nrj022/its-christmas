package com.itschristmas.card.cardeditor

import com.itschristmas.domain.model.Asset

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class MyObjectClicked(val assetId: Long): CardEditorIntent()
    data object DeleteClicked: CardEditorIntent()
    data object AdjustClicked: CardEditorIntent()
    data object AdjustCancelClicked: CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
}