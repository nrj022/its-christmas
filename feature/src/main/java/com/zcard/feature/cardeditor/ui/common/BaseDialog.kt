package com.zcard.feature.cardeditor.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.zcard.designsystem.theme.ZCardTheme

@Composable
fun BaseDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    content: String = "",
    confirmLabel: String = "",
    dismissLabel: String = "",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    customContent: (@Composable () -> Unit)? = null
) {
    AlertDialog(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        text = content.takeIf { it.isNotEmpty() }?.let {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = content, style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        } ?: customContent?.let { customContent },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel, style = MaterialTheme.typography.labelSmall)
            }
        },
        dismissButton = {
            if(dismissLabel.isNotBlank()) {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissLabel, style = MaterialTheme.typography.labelSmall)
                }
            } else null
        },
        onDismissRequest = onDismissRequest
    )
}

@Composable
@Preview(showBackground = true)
fun BaseDialogPreview() {
    ZCardTheme {
        BaseDialog(
            title = "Title",
            content = "",
            confirmLabel = "Confirm",
            dismissLabel = "Dismiss"
        )
    }
}