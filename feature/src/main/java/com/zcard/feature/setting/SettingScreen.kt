package com.zcard.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Launch
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.feature.R
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.designsystem.component.BaseDialog
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White

@Composable
fun SettingScreen(viewModel: SettingViewModel = hiltViewModel()) {
    val state by viewModel.settingState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.STARTED)

    SettingContent(
        onBackClicked = { viewModel.onIntent(SettingIntent.NavigateBack) },
        onOpenSourceCreditsClicked = { viewModel.onIntent(SettingIntent.OpenCreditsDocs) },
        onLeaveFeedbackClicked = { viewModel.onIntent(SettingIntent.ShowFeedbackDialog) },
    )

    if(state.showFeedbackDialog) {
        FeedbackDialog(
            feedbackText = state.feedbackText,
            onFeedbackTextChanged = { viewModel.onIntent(SettingIntent.OnFeedbackTextChanged(it)) },
            onSubmit = { viewModel.onIntent(SettingIntent.SubmitFeedback(it)) },
            onDismiss = { viewModel.onIntent(SettingIntent.CancelFeedback) },
            onDismissRequest = { viewModel.onIntent(SettingIntent.CancelFeedback) }
        )
    }
}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
    onOpenSourceCreditsClicked: () -> Unit = {},
    onLeaveFeedbackClicked: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        topBar = { TopBar(onBackClicked = onBackClicked) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MenuItem(
                label = stringResource(R.string.setting_label_open_source_credits),
                icon = Icons.AutoMirrored.Rounded.Launch,
                onClick = onOpenSourceCreditsClicked
            )
            MenuItem(
                label = stringResource(R.string.setting_label_leave_feedback),
                icon = Icons.Default.ChatBubbleOutline,
                onClick = onLeaveFeedbackClicked
            )
        }
    }
}

@Composable
fun TopBar(onBackClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClicked,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_cd_back_button),
                tint = SoftBlack
            )
        }
    }
}

@Composable
fun MenuItem(label: String = "", icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SoftBlack
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun FeedbackDialog(
    modifier: Modifier = Modifier,
    feedbackText: String = "",
    onFeedbackTextChanged: (String) -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    BaseDialog(
        modifier = modifier,
        title = stringResource(R.string.setting_dialog_title_feedback),
        confirmLabel = stringResource(R.string.setting_dialog_button_submit),
        dismissLabel = stringResource(R.string.common_button_cancel),
        onConfirm = { onSubmit(feedbackText) },
        onDismiss = onDismiss,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.setting_dialog_content_feedback),
                style = MaterialTheme.typography.labelSmall,
                color = SoftBlack
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(10.dp)),
                value = feedbackText,
                textStyle = MaterialTheme.typography.labelSmall,
                onValueChange = onFeedbackTextChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.setting_dialog_placeholder_feedback),
                        style = MaterialTheme.typography.labelSmall,
                        color = White
                    )
                },
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
}

@Preview(showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    ZCardTheme {
        SettingContent()
    }
}