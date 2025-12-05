package com.itschristmas.card.cardeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itschristmas.card.cardeditor.model.PanelState
import com.itschristmas.card.cardeditor.panels.AssetBrowserPanel
import com.itschristmas.card.cardeditor.panels.TransformControlPanel
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.card.cardeditor.dialog.ObjectDeleteConfirmDialog
import com.itschristmas.card.cardeditor.dialog.SetCardTitleDialog
import com.itschristmas.card.cardeditor.dialog.UnsavedChangesDialog

@Composable
fun CardEditorBottomScreen(cardId: Long, viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(CardEditorIntent.Init(cardId))
    }

    when (state.panelState) {
        PanelState.ASSET_BROWSER ->
            AssetBrowserPanel(
                objectItems = state.objects, // 임시 데이터
                backgroundItems = state.backgrounds,  // 임시 데이터
                myObjects = state.myObjects,
                selectedMyObject = state.selectedMyObjectIdx,
                selectedBackground = state.selectedBackground,
                onNextClicked = {},
                onObjectClicked = { viewModel.onIntent(CardEditorIntent.ObjectClicked(cardId, it)) },
                onMyObjectClicked = { viewModel.onIntent(CardEditorIntent.MyObjectClicked(it)) },
                onBackgroundClicked = { viewModel.onIntent(CardEditorIntent.BackgroundClicked(it)) }
            )

        PanelState.TRANSFORM_CONTROL -> {
            TransformControlPanel(
                scale = state.tempTransformState?.scale ?: 1,
                onScaleChange = { newScale ->
                    viewModel.onIntent(CardEditorIntent.ScaleChanged(newScale))
                },
                onCancel = { viewModel.onIntent(CardEditorIntent.AdjustCancelClicked) },
                onApply = { viewModel.onIntent(CardEditorIntent.AdjustApplyClicked) },
                onDirectionalClick = { direction ->
                    viewModel.onIntent(CardEditorIntent.DirectionalClicked(direction))
                },
                onCameraReset = { viewModel.onIntent(CardEditorIntent.CameraResetClicked) }
            )
        }
        PanelState.TEXT_EDITOR -> {}
    }

    when(state.editorDialogState) {
        EditorDialogState.NONE -> {}
        EditorDialogState.DELETE_CONFIRM -> {
            ObjectDeleteConfirmDialog(
                onDeleteObject = { viewModel.onIntent(CardEditorIntent.DeleteMyObject) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.DialogStateChanged(EditorDialogState.NONE)) }
            )
        }
        EditorDialogState.UNSAVED_TRANSFORM_CHANGES -> {
            UnsavedChangesDialog(
                onApplyChanges = { viewModel.onIntent(CardEditorIntent.AdjustApplyAndExit) },
                onDiscardChanges = { viewModel.onIntent(CardEditorIntent.AdjustDiscardAndExit) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.DialogStateChanged(EditorDialogState.NONE)) }
            )
        }
        EditorDialogState.UNSAVED_TEXT_CHANGES -> {
            UnsavedChangesDialog(
                onApplyChanges = { },
                onDiscardChanges = { },
                onDismiss = { }
            )
        }
        EditorDialogState.SET_CARD_TITLE -> {
            SetCardTitleDialog(
                cardTitle = state.cardTitle,
                onTitleChange = { viewModel.onIntent(CardEditorIntent.CardTitleChanged(it)) },
                onGenerateCard = { viewModel.onIntent(CardEditorIntent.CardGenerateClicked) },
                onDismiss = { viewModel.onIntent(CardEditorIntent.DialogStateChanged(EditorDialogState.NONE)) }
            )
        }
    }
}