package com.itschristmas.card.cardeditor.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itschristmas.card.cardeditor.model.Direction
import com.itschristmas.designsystem.theme.ItsChristmasTheme

enum class Axis(val label: String) {
    XY("x/y"), YZ("y/z")
}

/**
 * 상하좌우 방향키 컨트롤러
 */
@Composable
fun DirectionalController(
    modifier: Modifier = Modifier,
    onClick: (Direction) -> Unit,
) {
    var axis by remember { mutableStateOf(Axis.XY) }

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
                .clickable { axis = if(axis == Axis.XY) Axis.YZ else Axis.XY },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = axis.label,
                style = MaterialTheme.typography.labelSmall
            )
        }

        // 방향 버튼들
        ControlButton(
            direction = Direction.UP,
            onClick = onClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        ControlButton(
            direction = if(axis == Axis.XY) Direction.LEFT else Direction.FORWARD,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        ControlButton(
            direction = if(axis == Axis.XY) Direction.RIGHT else Direction.BACKWARD,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        ControlButton(
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
    direction: Direction,
    onClick: (Direction) -> Unit
) {
    IconButton(
        onClick = { onClick(direction) },
        modifier = modifier
            .semantics { contentDescription = "$direction Button" }
            .fillMaxSize(0.3f)
            .shadow(elevation = 3.dp, shape = CircleShape)
            .background(Color.White, CircleShape),
    ) {
        Text(
            text = direction.name[0].toString(),
            style = MaterialTheme.typography.titleLarge
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
        )
    }
}
