package com.itschristmas.card.cardeditor

import com.itschristmas.card.cardeditor.model.DialogState
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.domain.model.Asset
import com.itschristmas.domain.model.CardElementWithAssetKeys
import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption
import com.itschristmas.domain.model.UnityStatusType

sealed class CardEditorIntent {
    data class Init(val cardId: Long): CardEditorIntent()
    data class ChangeTitle(val newTitle: String): CardEditorIntent()

    data object OpenCardLinkDetail: CardEditorIntent()
    data object SaveTitle: CardEditorIntent()
    data object CopyCardLink: CardEditorIntent()
    data object ResetTitle: CardEditorIntent()

    data object FinishEditing: CardEditorIntent()
    data object ExportGlbAndUpload: CardEditorIntent()
    data class ExportGlbResult(val unityStatusType: UnityStatusType, val result: String): CardEditorIntent()
    data class ChangeDialogState(val dialogState: DialogState): CardEditorIntent()

    data class CreateObject(val clickedObject: Asset): CardEditorIntent()
    data class ChangeBackground(val assetId: Long): CardEditorIntent()
    data class SelectSpawnedObject(val element: CardElementWithAssetKeys): CardEditorIntent()
    data object DeleteSpawnedObject: CardEditorIntent()
    data object EnterTransformMode: CardEditorIntent()
    data object EnterTextMode: CardEditorIntent()

    data object ResetCamera: CardEditorIntent()

    /* 오브젝트 조정 */
    data class MoveObject(val direction: Direction): CardEditorIntent()
    data class ChangeScale(val newScale: Int): CardEditorIntent()
    data object CancelTransform: CardEditorIntent()
    data object ApplyTransform: CardEditorIntent()
    data object ApplyAndExitTransform: CardEditorIntent()
    data object DiscardAndExitTransform: CardEditorIntent()
    data object ResetTransform: CardEditorIntent()
    data object CameraFocus: CardEditorIntent()

    /* 텍스트 편집 */
    data object MissingTextSelection: CardEditorIntent()
    data object AddText: CardEditorIntent()
    data class DeleteText(val textId: Long): CardEditorIntent()
    data class SelectText(val textId: Long): CardEditorIntent()
    data class ChangeTextContent(val newText: String): CardEditorIntent()
    data class SelectAlignment(val newAlignment: TextAlignmentOption): CardEditorIntent()
    data class SelectColor(val newColor: ColorOption): CardEditorIntent()
    data class ChangeFontSize(val newSize: Float): CardEditorIntent()
    data class SelectFont(val newFont: FontOption): CardEditorIntent()
    data class MoveText(val direction: Direction): CardEditorIntent()
    data object ApplyText: CardEditorIntent()
    data object ApplyAndExitText: CardEditorIntent()
    data object DiscardAndExitText: CardEditorIntent()
}