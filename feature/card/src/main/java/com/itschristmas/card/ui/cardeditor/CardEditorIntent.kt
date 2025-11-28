package com.itschristmas.card.ui.cardeditor

import com.itschristmas.domain.model.Asset

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class MyObjectClicked(val assetId: Long): CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
}