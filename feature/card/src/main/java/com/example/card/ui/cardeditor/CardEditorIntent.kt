package com.example.card.ui.cardeditor

import com.example.domain.model.Asset

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class MyObjectClicked(val assetId: Long): CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
}