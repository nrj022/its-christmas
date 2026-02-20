package com.itschristmas.card.cardeditor.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itschristmas.card.R
import com.itschristmas.card.cardeditor.ui.common.BaseDialog
import com.itschristmas.designsystem.theme.Gray
import com.itschristmas.designsystem.theme.SoftBlack
import com.itschristmas.designsystem.theme.White

@Composable
fun CardInfoDialog(
    cardTitle: String = "",
    isTitleChanged: Boolean = false,
    onTitleChange: (String) -> Unit,
    onTitleSave: () -> Unit,
    onCopyLink: () -> Unit,
    onDismiss: () -> Unit
) {
    val containerColor = if (isTitleChanged) SoftBlack else Gray
    val contentColor = if (isTitleChanged) White else SoftBlack

    BaseDialog(
        title = stringResource(R.string.editor_dialog_title_link_detail),
        confirmLabel = "Confirm",
        onConfirm = onDismiss,
        onDismissRequest = onDismiss
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    value = cardTitle,
                    textStyle = MaterialTheme.typography.labelSmall,
                    onValueChange = onTitleChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.editor_dialog_placeholder_card_title),
                            style = MaterialTheme.typography.labelSmall
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
                Button(
                    enabled = isTitleChanged,
                    onClick = onTitleSave,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.editor_dialog_button_title_save),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onCopyLink() }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_copy),
                        contentDescription = "",
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(R.string.editor_dialog_button_link_copy),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Text(
                    text = stringResource(R.string.editor_dialog_text_exclude_new_edit),
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CardInfoDialogPreview() {
    CardInfoDialog(
        onTitleChange = {},
        onTitleSave = {},
        onDismiss = {},
        onCopyLink = {}
    )
}
