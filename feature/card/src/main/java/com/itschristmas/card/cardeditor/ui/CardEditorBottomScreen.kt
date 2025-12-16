package com.itschristmas.card.cardeditor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itschristmas.card.cardeditor.CardEditorIntent
import com.itschristmas.card.cardeditor.CardEditorViewModel
import com.itschristmas.card.cardeditor.model.PanelType
import com.itschristmas.card.cardeditor.ui.panels.AssetBrowserPanel
import com.itschristmas.card.cardeditor.ui.panels.TransformControlPanel
import com.itschristmas.card.cardeditor.model.DialogState
import com.itschristmas.card.cardeditor.ui.dialog.ObjectDeleteConfirmDialog
import com.itschristmas.card.cardeditor.ui.dialog.SetCardTitleDialog
import com.itschristmas.card.cardeditor.ui.dialog.UnsavedChangesDialog
import com.itschristmas.card.cardeditor.ui.panels.TextEditorPanel

@Composable
fun CardEditorBottomScreen(cardId: Long, viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(CardEditorIntent.Init(cardId))
    }

    when (state.panelType) {
        PanelType.ASSET_BROWSER ->
            AssetBrowserPanel(
                objectItems = state.objects, // 임시 데이터
                backgroundItems = state.backgrounds,  // 임시 데이터
                spawnedObjects = state.spawnedObjects,
                selectedSpawnedObject = state.selectedSpawnedObjectId,
                selectedBackground = state.selectedBackgroundId,
                onAddTextClicked = { viewModel.onIntent(CardEditorIntent.EnterTextMode) },
                onObjectClicked = { viewModel.onIntent(CardEditorIntent.CreateObject(cardId, it)) },
                onSpawnedObjectClicked = { viewModel.onIntent(CardEditorIntent.SelectSpawnedObject(it)) },
                onBackgroundClicked = { viewModel.onIntent(CardEditorIntent.ChangeBackground(cardId, it)) }
            )

        PanelType.TRANSFORM_CONTROL -> {
            TransformControlPanel(
                scale = state.tempTransform?.scale ?: 1,
                onScaleChange = { newScale ->
                    viewModel.onIntent(CardEditorIntent.ChangeScale(newScale))
                },
                onCancel = { viewModel.onIntent(CardEditorIntent.CancelTransform) },
                onApply = { viewModel.onIntent(CardEditorIntent.ApplyTransform) },
                onDirectionalClick = { direction ->
                    viewModel.onIntent(CardEditorIntent.MoveObject(direction))
                },
                onCameraReset = { viewModel.onIntent(CardEditorIntent.ResetCamera) }
            )
        }

        PanelType.TEXT_EDITOR -> {
            val tempText = state.selectedText
            if(state.tempTextList.isEmpty() || tempText == null) {
                LaunchedEffect(state.panelType, state.tempTextList, state.selectedText) {
                    viewModel.onIntent(CardEditorIntent.RequestDefaultText)
                }
            } else {
                TextEditorPanel(
                    textElement = tempText.textElement,
                    onTextChange = { viewModel.onIntent(CardEditorIntent.ChangeTextContent(it)) },
                    onAlignmentSelected = { viewModel.onIntent(CardEditorIntent.SelectAlignment(it)) },
                    onColorSelected = { viewModel.onIntent(CardEditorIntent.SelectColor(it)) },
                    onFontSizeChange = { viewModel.onIntent(CardEditorIntent.ChangeFontSize(it)) },
                    onFontSelected = { viewModel.onIntent(CardEditorIntent.SelectFont(it)) },
                    onPositionChange = { viewModel.onIntent(CardEditorIntent.MoveText(it)) },
                    onCameraReset = { viewModel.onIntent(CardEditorIntent.ResetCamera) },
                    onApply = { viewModel.onIntent(CardEditorIntent.ApplyText(cardId)) },
                    onBack = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.UNSAVED_TEXT_CHANGES)) }
                )
            }
        }
    }

    when(state.dialogState) {
        DialogState.NONE -> {}
        DialogState.DELETE_CONFIRM -> {
            ObjectDeleteConfirmDialog(
                onDeleteObject = { viewModel.onIntent(CardEditorIntent.DeleteSpawnedObject) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE)) }
            )
        }
        DialogState.UNSAVED_TRANSFORM_CHANGES -> {
            UnsavedChangesDialog(
                onApplyChanges = { viewModel.onIntent(CardEditorIntent.ApplyAndExitTransform) },
                onDiscardChanges = { viewModel.onIntent(CardEditorIntent.DiscardAndExitTransform) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE)) }
            )
        }
        DialogState.UNSAVED_TEXT_CHANGES -> {
            UnsavedChangesDialog(
                onApplyChanges = { viewModel.onIntent(CardEditorIntent.ApplyAndExitText(cardId)) },
                onDiscardChanges = { viewModel.onIntent(CardEditorIntent.DiscardAndExitText(cardId)) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE)) }
            )
        }
        DialogState.SET_CARD_TITLE -> {
            SetCardTitleDialog(
                cardTitle = state.cardTitle,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.ChangeTitle(it)) },
                onGenerateCard = { viewModel.onIntent(CardEditorIntent.ExportGlbAndUpload) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.NONE)) }
            )
        }
    }
}