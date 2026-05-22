package com.zcard.feature.cardeditor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.feature.cardeditor.TransformIntent
import com.zcard.feature.cardeditor.TransformViewModel
import com.zcard.feature.cardeditor.ui.panels.TransformControlPanel
import com.zcard.feature.cardeditor.ui.dialog.UnsavedChangesDialog

@Composable
fun TransformScreen(viewModel: TransformViewModel = hiltViewModel()) {
    val state by viewModel.transformState.collectAsStateWithLifecycle()

    TransformControlPanel(
        scale = state.tempTransform.scale,
        onScaleChange = { newScale ->
            viewModel.onIntent(TransformIntent.ChangeScale(newScale))
        },
        onCancel = { viewModel.onIntent(TransformIntent.Exit) },
        onApply = { viewModel.onIntent(TransformIntent.SaveChanges) },
        onDirectionalClick = { direction ->
            viewModel.onIntent(TransformIntent.MoveObject(direction))
        }
    )

    if(state.showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onApplyChanges = { viewModel.onIntent(TransformIntent.SaveAndExit) },
            onDiscardChanges = { viewModel.onIntent(TransformIntent.DiscardAndExit) },
            onDismiss = { viewModel.onIntent(TransformIntent.DismissDialog) }
        )
    }
}