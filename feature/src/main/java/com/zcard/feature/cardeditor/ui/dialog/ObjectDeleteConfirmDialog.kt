package com.zcard.feature.cardeditor.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zcard.feature.cardeditor.ui.common.BaseDialog
import com.zcard.feature.R

@Composable
fun ObjectDeleteConfirmDialog(
    onDeleteObject: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(
        title = stringResource(R.string.editor_dialog_title_delete),
        confirmLabel = stringResource(R.string.editor_dialog_button_delete),
        dismissLabel = stringResource(R.string.common_button_cancel),
        onConfirm = onDeleteObject,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss
    )
}