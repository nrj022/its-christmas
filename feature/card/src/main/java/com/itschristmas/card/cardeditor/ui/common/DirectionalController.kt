package com.itschristmas.card.cardeditor.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.designsystem.theme.ItsChristmasTheme

/**
 * 상하좌우 방향키 컨트롤러
 */
@Composable
fun DirectionalController(
    modifier: Modifier = Modifier,
    onClick: (Direction) -> Unit,
    onCameraReset: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // 가운데 작은 회색 원
        Box(
            modifier = Modifier
                .fillMaxSize(0.2f)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .clickable { onCameraReset() }
        )

        // 방향 버튼들
        ControlButton(
            icon = Icons.Default.KeyboardArrowUp,
            direction = Direction.UP,
            onClick = onClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        ControlButton(
            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            direction = Direction.LEFT,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        ControlButton(
            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            direction = Direction.RIGHT,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        ControlButton(
            icon = Icons.Default.KeyboardArrowDown,
            direction = Direction.DOWN,
            onClick = onClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 방향키에 사용되는 원형 아이콘 버튼
 */
@Composable
private fun ControlButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    direction: Direction,
    onClick: (Direction) -> Unit
) {
    IconButton(
        onClick = { onClick(direction) },
        modifier = modifier
            .fillMaxSize(0.3f)
            .shadow(elevation = 3.dp, shape = CircleShape)
            .background(Color.White, CircleShape)
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(0.64f),
            imageVector = icon,
            contentDescription = "$direction Button",
            tint = Color.Black
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DirectionControllerPreview() {
    ItsChristmasTheme {
        DirectionalController(
            modifier = Modifier.size(220.dp),
            onClick = {},
            onCameraReset = {}
        )
    }
}
