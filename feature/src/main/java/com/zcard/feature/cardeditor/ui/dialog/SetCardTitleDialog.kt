package com.zcard.feature.cardeditor.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zcard.card.R
import com.zcard.feature.cardeditor.ui.common.BaseDialog
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.White

@Composable
fun SetCardTitleDialog(
    cardTitle: String = "",
    onTitleChange: (String) -> Unit,
    onGenerateCard: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseDialog(
        title = stringResource(R.string.editor_dialog_title_set_card_title),
        confirmLabel = stringResource(R.string.editor_dialog_button_generate),
        dismissLabel = stringResource(R.string.common_button_cancel),
        onConfirm = onGenerateCard,
        onDismiss = onDismiss,
        onDismissRequest = onDismiss
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = cardTitle,
            textStyle = MaterialTheme.typography.labelSmall,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.editor_dialog_placeholder_card_title),
                    style = MaterialTheme.typography.labelSmall,
                    color = White
                )
            },
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Gray,
                focusedContainerColor = Gray
            )
        )
    }
}
