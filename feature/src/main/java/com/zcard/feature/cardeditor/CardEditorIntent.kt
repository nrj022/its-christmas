package com.zcard.feature.cardeditor

import com.zcard.domain.model.Asset
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.domain.bridge.UnityMessage

sealed class CardEditorIntent {
    data class OnUnityMessage(val message: UnityMessage) : CardEditorIntent()
    data class Init(val cardId: Long): CardEditorIntent()
    data object BackPressed: CardEditorIntent()
    data class ChangeTitle(val newTitle: String): CardEditorIntent()

    data object OpenCardLinkDetail: CardEditorIntent()
    data object SaveTitle: CardEditorIntent()
    data object NavigateToCardShare: CardEditorIntent()
    data object ResetTitle: CardEditorIntent()

    data object FinishEditing: CardEditorIntent()
    data object ExportGlbAndUpload: CardEditorIntent()
    data object CloseLoading: CardEditorIntent()
    data object CancelUpload: CardEditorIntent()
    data class ChangeDialogState(val dialogState: CardEditorState.DialogState): CardEditorIntent()

    data class ChangeTab(val tab: CardEditorState.AssetBrowserTab): CardEditorIntent()
    data class CreateObject(val clickedObject: Asset): CardEditorIntent()
    data class ChangeBackground(val assetId: Long): CardEditorIntent()
    data class SelectSpawnedObject(val element: CardElementWithAssetKeys): CardEditorIntent()
    data object DeleteSpawnedObject: CardEditorIntent()
    data object EnterTransformMode: CardEditorIntent()
    data object EnterTextMode: CardEditorIntent()

    data object ResetCamera: CardEditorIntent()
}