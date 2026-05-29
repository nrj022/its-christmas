package com.zcard.feature.cardeditor.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zcard.feature.R
import com.zcard.designsystem.component.BaseDialog

@Composable
fun UploadCancelConfirmDialog(
    onCancelUpload: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(
        title = stringResource(R.string.editor_dialog_title_cancel_upload),
        content = stringResource(R.string.editor_dialog_content_cancel_upload),
        confirmLabel = stringResource(R.string.common_button_yes),
        dismissLabel = stringResource(R.string.common_button_cancel),
        onConfirm = onCancelUpload,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss
    )
}