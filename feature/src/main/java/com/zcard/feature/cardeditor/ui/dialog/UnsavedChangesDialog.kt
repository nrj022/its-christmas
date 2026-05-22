package com.zcard.feature.cardeditor.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zcard.feature.R
import com.zcard.designsystem.component.BaseDialog

@Composable
fun UnsavedChangesDialog(
    onApplyChanges: () -> Unit,
    onDiscardChanges: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(
        title = stringResource(R.string.editor_dialog_title_unsaved_changes),
        content = stringResource(R.string.editor_dialog_content_unsaved_changes),
        confirmLabel = stringResource(R.string.editor_dialog_button_save_confirm),
        dismissLabel = stringResource(R.string.common_button_no),
        onConfirm = onApplyChanges,
        onDismiss = onDiscardChanges,
        onDismissRequest = onDismiss
    )
}