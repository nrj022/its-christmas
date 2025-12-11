package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class CardTitleChanged(val newTitle: String): CardEditorIntent()
    data object CompleteClicked: CardEditorIntent()
    data object GenerateCard: CardEditorIntent()
    data class ObjectClicked(val cardId: Long, val clickedObject: Asset): CardEditorIntent()
    data class BackgroundClicked(val assetId: Long): CardEditorIntent()
    data class AddTextButtonClicked(val cardId: Long): CardEditorIntent()
    data class MyObjectClicked(val element: CardElementWithAssetKeys): CardEditorIntent()
    data class DialogStateChanged(val dialogState: EditorDialogState): CardEditorIntent()
    data object DeleteMyObject: CardEditorIntent()
    data object AdjustClicked: CardEditorIntent()
    data object AdjustCancelClicked: CardEditorIntent()
    data object AdjustApplyClicked: CardEditorIntent()
    data object AdjustApplyAndExit: CardEditorIntent()
    data object AdjustDiscardAndExit: CardEditorIntent()
    data class ObjectDirectionClicked(val direction: Direction): CardEditorIntent()
    data class ScaleChanged(val newScale: Int): CardEditorIntent()
    data object CameraResetClicked: CardEditorIntent()
    data object TransformResetClicked: CardEditorIntent()
    data object RequestDefaultText: CardEditorIntent()
    data object AddText: CardEditorIntent()
    data class DeleteText(val textId: Long): CardEditorIntent()
    data class TextClicked(val textId: Long): CardEditorIntent()
    data class TextChanged(val newText: String): CardEditorIntent()
    data class AlignmentSelected(val newAlignment: TextAlignmentOption): CardEditorIntent()
    data class ColorSelected(val newColor: ColorOption): CardEditorIntent()
    data class FontSizeChanged(val newSize: Float): CardEditorIntent()
    data class FontSelected(val newFont: FontOption): CardEditorIntent()
    data class TextDirectionClicked(val direction: Direction): CardEditorIntent()
    data class TextApplyClicked(val cardId: Long): CardEditorIntent()
    data class TextApplyAndExit(val cardId: Long): CardEditorIntent()
    data object TextDiscardAndExit: CardEditorIntent()
}