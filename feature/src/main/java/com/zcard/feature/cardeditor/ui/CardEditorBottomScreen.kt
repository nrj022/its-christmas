package com.zcard.feature.cardeditor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.feature.cardeditor.CardEditorIntent
import com.zcard.feature.cardeditor.CardEditorViewModel
import com.zcard.feature.cardeditor.ui.panels.AssetBrowserPanel
import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.ui.dialog.CardInfoDialog
import com.zcard.feature.cardeditor.ui.dialog.ObjectDeleteConfirmDialog
import com.zcard.feature.cardeditor.ui.dialog.SetCardTitleDialog

@Composable
fun CardEditorBottomScreen(viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    AssetBrowserPanel(
        objectItems = state.objects,
        backgroundItems = state.backgrounds,
        spawnedObjects = state.spawnedObjects,
        selectedSpawnedObject = state.selectedSpawnedObjectId,
        selectedBackground = state.selectedBackgroundId,
        loadingObjectIds = state.loadingObjectIds,
        onAddTextClicked = { viewModel.onIntent(CardEditorIntent.EnterTextMode) },
        onObjectClicked = { viewModel.onIntent(CardEditorIntent.CreateObject(it)) },
        onSpawnedObjectClicked = { viewModel.onIntent(CardEditorIntent.SelectSpawnedObject(it)) },
        onBackgroundClicked = { viewModel.onIntent(CardEditorIntent.ChangeBackground(it)) }
    )

    when(state.dialogState) {
        DialogState.NONE -> {}
        DialogState.DELETE_CONFIRM -> {
            ObjectDeleteConfirmDialog(
                onDeleteObject = { viewModel.onIntent(CardEditorIntent.DeleteSpawnedObject) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE)) }
            )
        }
        DialogState.CARD_LINK_DETAIL -> {
            CardInfoDialog(
                cardTitle = state.cardTitle,
                isTitleChanged = state.isTitleChanged,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.ChangeTitle(it)) },
                onTitleSave = { viewModel.onIntent(CardEditorIntent.SaveTitle) },
                onCopyLink = { viewModel.onIntent(CardEditorIntent.CopyCardLink) },
                onDismiss = {
                    viewModel.onIntent(CardEditorIntent.ResetTitle)
                    viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE))
                }
            )
        }
        DialogState.SET_CARD_TITLE -> {
            SetCardTitleDialog(
                cardTitle = state.cardTitle,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.ChangeTitle(it)) },
                onGenerateCard = { viewModel.onIntent(CardEditorIntent.ExportGlbAndUpload) },
                onDismiss = {
                    viewModel.onIntent(CardEditorIntent.ResetTitle)
                    viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE))
                }
            )
        }
    }
}