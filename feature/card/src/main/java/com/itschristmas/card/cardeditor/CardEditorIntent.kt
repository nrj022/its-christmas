package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElementWithAssetKeys

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class CardTitleChanged(val newTitle: String): CardEditorIntent()
    data object CompleteClicked: CardEditorIntent()
    data object GenerateCard: CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
    data object AddTextClicked: CardEditorIntent()
    data class MyObjectClicked(val element: CardElementWithAssetKeys): CardEditorIntent()
    data class DialogStateChanged(val dialogState: EditorDialogState): CardEditorIntent()
    data object DeleteMyObject: CardEditorIntent()
    data object AdjustClicked: CardEditorIntent()
    data object AdjustCancelClicked: CardEditorIntent()
    data object AdjustApplyClicked: CardEditorIntent()
    data object AdjustApplyAndExit: CardEditorIntent()
    data object AdjustDiscardAndExit: CardEditorIntent()
    data class DirectionalClicked(val direction: Direction): CardEditorIntent()
    data class ScaleChanged(val newScale: Int): CardEditorIntent()
    data object CameraResetClicked: CardEditorIntent()
    data object TransformResetClicked: CardEditorIntent()
}