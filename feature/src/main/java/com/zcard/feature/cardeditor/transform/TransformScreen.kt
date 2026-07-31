package com.zcard.feature.cardeditor.transform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.R
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.feature.cardeditor.component.DirectionalController
import com.zcard.feature.cardeditor.dialog.UnsavedChangesDialog

@Composable
fun TransformScreen(viewModel: TransformViewModel = hiltViewModel()) {
    val state by viewModel.transformState.collectAsStateWithLifecycle()

    TransformContent(
        scale = state.tempTransform.scale,
        transformToggleType = state.transformToggleType,
        onScaleChange = { newScale ->
            viewModel.onIntent(TransformIntent.ChangeScale(newScale))
        },
        onDirectionalClick = { direction ->
            viewModel.onIntent(TransformIntent.MoveObject(direction))
        },
        onRotationDragStart = {
            viewModel.onIntent(TransformIntent.StartRotationDrag)
        },
        onRotationChange = { newRotation ->
            viewModel.onIntent(TransformIntent.RotateObject(newRotation))
        },
        onCancel = { viewModel.onIntent(TransformIntent.Exit) },
        onToggle = { viewModel.onIntent(TransformIntent.ToggleTransformType) },
        onApply = { viewModel.onIntent(TransformIntent.SaveChanges) },
    )

    if(state.showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onApplyChanges = { viewModel.onIntent(TransformIntent.SaveAndExit) },
            onDiscardChanges = { viewModel.onIntent(TransformIntent.DiscardAndExit) },
            onDismiss = { viewModel.onIntent(TransformIntent.DismissDialog) }
        )
    }
}

@Composable
fun TransformContent(
    scale: Int = 1,
    transformToggleType: TransformState.TransformType = TransformState.TransformType.ROTATION,
    onScaleChange: (Int) -> Unit = {},
    onDirectionalClick: (Direction) -> Unit = {},
    onRotationDragStart: () -> Unit = {},
    onRotationChange: (Rotation) -> Unit = {},
    onCancel: () -> Unit = {},
    onToggle: () -> Unit = {},
    onApply: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 Cancel, Apply 버튼
        TopActionRow(
            toggleType = transformToggleType,
            onCancel = onCancel,
            onToggle = onToggle,
            onApply = onApply
        )

        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp) // 방향키와 수량 조절기 사이 간격
        ) {
            if(transformToggleType != TransformState.TransformType.TRANSLATION) {
                DirectionalController(onClick = onDirectionalClick)
                ScaleController(
                    scale = scale,
                    onScaleChange = onScaleChange
                )
            } else {
                RotationGizmo(
                    onDragStart = onRotationDragStart,
                    onRotate = onRotationChange,
                )
            }
        }
    }
}

/**
 * 상단의 Cancel, Apply 텍스트 버튼 Row
 */
@Composable
private fun TopActionRow(
    toggleType: TransformState.TransformType,
    onCancel: () -> Unit,
    onToggle: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(R.string.common_button_cancel),
                style = MaterialTheme.typography.labelSmall,
                color = SoftBlack
            )
        }
        Button(
            onClick = onToggle,
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(containerColor = Gray, contentColor = SoftBlack),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(toggleType.icon),
                    contentDescription = null,
                    tint = SoftBlack
                )
                Text(
                    text = stringResource(toggleType.label),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        TextButton(onClick = onApply) {
            Text(
                text = stringResource(R.string.common_button_apply),
                style = MaterialTheme.typography.labelSmall,
                color = SoftBlack
            )
        }
    }
}

/**
 * Scale 표시 및 조절 컨트롤러 (+, 1, -)
 */
@Composable
private fun ScaleController(
    scale: Int,
    onScaleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // + 버튼
        ScaleAdjustButton(
            icon = Icons.Default.Add,
            contentDescription = stringResource(R.string.transform_cd_increase_scale),
            onClick = { onScaleChange(scale + 1) }
        )

        // 숫자 표시
        Text(
            text = scale.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // - 버튼
        ScaleAdjustButton(
            icon = Icons.Default.Remove,
            contentDescription = stringResource(R.string.transform_cd_decrease_scale),
            onClick = { onScaleChange(scale - 1) }
        )
    }
}

/**
 * Scale 조절에 사용되는 사각형 아이콘 버튼 (+, -)
 */
@Composable
private fun ScaleAdjustButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Gray.copy(alpha = 0.5f),
            contentColor = SoftBlack
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TransformPreview() {
    ZCardTheme {
        TransformContent()
    }
}