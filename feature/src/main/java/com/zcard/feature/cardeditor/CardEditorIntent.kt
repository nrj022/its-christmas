package com.zcard.feature.cardeditor

import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.domain.model.Asset
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextFontFamily
import com.zcard.domain.model.TextAlignment
import com.zcard.domain.bridge.UnityMessage

sealed class CardEditorIntent {
    data class OnUnityMessage(val message: UnityMessage) : CardEditorIntent()
    data class Init(val cardId: Long): CardEditorIntent()
    data class ChangeTitle(val newTitle: String): CardEditorIntent()

    data object OpenCardLinkDetail: CardEditorIntent()
    data object SaveTitle: CardEditorIntent()
    data object CopyCardLink: CardEditorIntent()
    data object ResetTitle: CardEditorIntent()

    data object FinishEditing: CardEditorIntent()
    data object ExportGlbAndUpload: CardEditorIntent()
    data class ChangeDialogState(val dialogState: DialogState): CardEditorIntent()

    data class CreateObject(val clickedObject: Asset): CardEditorIntent()
    data class ChangeBackground(val assetId: Long): CardEditorIntent()
    data class SelectSpawnedObject(val element: CardElementWithAssetKeys): CardEditorIntent()
    data object DeleteSpawnedObject: CardEditorIntent()
    data object EnterTransformMode: CardEditorIntent()
    data object EnterTextMode: CardEditorIntent()

    data object ResetCamera: CardEditorIntent()

    /* 오브젝트 조정 패널 */
    data class MoveObject(val direction: Direction): CardEditorIntent()
    data class ChangeScale(val newScale: Int): CardEditorIntent()
    data object CancelTransform: CardEditorIntent()
    data object ApplyTransform: CardEditorIntent()
    data object ApplyAndExitTransform: CardEditorIntent()
    data object DiscardAndExitTransform: CardEditorIntent()
    data object ResetTransform: CardEditorIntent()
    data object CameraFocus: CardEditorIntent()

    /* 텍스트 편집 패널 */
    data object MissingTextSelection: CardEditorIntent()
    data object AddText: CardEditorIntent()
    data class DeleteText(val textId: Long): CardEditorIntent()
    data class SelectText(val textId: Long): CardEditorIntent()
    data class ChangeTextContent(val newText: String): CardEditorIntent()
    data class SelectAlignment(val newAlignment: TextAlignment): CardEditorIntent()
    data class SelectColor(val newColor: TextColor): CardEditorIntent()
    data class ChangeFontSize(val newSize: Float): CardEditorIntent()
    data class SelectFont(val newFont: TextFontFamily): CardEditorIntent()
    data class MoveText(val direction: Direction): CardEditorIntent()
    data object ApplyText: CardEditorIntent()
    data object ApplyAndExitText: CardEditorIntent()
    data object DiscardAndExitText: CardEditorIntent()
}