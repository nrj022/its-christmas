package com.itschristmas.card.ui.cardeditor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 방향을 나타내는 Enum 클래스 (코드 가독성을 위해 사용)
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

/**
 * 이미지에 표시된 컨트롤러 전체 UI
 * @param scale 현재 수량 (가운데 숫자)
 * @param onScaleChange 수량 변경 시 호출되는 콜백 (+, - 버튼)
 * @param onDirectionalClick 방향키 클릭 시 호출되는 콜백
 * @param onReset 가운데 원(원래 상태로) 클릭 시 호출되는 콜백
 * @param onCancel 'Cancel' 버튼 클릭 시 호출되는 콜백
 * @param onApply 'Apply' 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun TransformControlPanel(
    scale: Int = 1,
    onScaleChange: (Int) -> Unit = {},
    onDirectionalClick: (Direction) -> Unit = {},
    onReset: () -> Unit = {},
    onCancel: () -> Unit = {},
    onApply: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 상단 Cancel, Apply 버튼
        TopActionRow(
            onCancel = onCancel,
            onApply = onApply
        )
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp) // 방향키와 수량 조절기 사이 간격
        ) {
            // 2-1. 방향키 컨트롤러
            DirectionalController(onClick = onDirectionalClick, onReset = onReset)

            // 2-2. Scale 조절기
            ScaleController(
                scale = scale,
                onScaleChange = onScaleChange
            )
        }
    }
}

/**
 * 상단의 Cancel, Apply 텍스트 버튼 Row
 */
@Composable
private fun TopActionRow(
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel) {
            Text(text = "Cancel", color = Color.Black, fontSize = 16.sp)
        }
        TextButton(onClick = onApply) {
            Text(text = "Apply", color = Color.Black, fontSize = 16.sp)
        }
    }
}

/**
 * 상하좌우 방향키 컨트롤러
 */
@Composable
private fun DirectionalController(
    onClick: (Direction) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(220.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // 가운데 작은 회색 원
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .clickable { onReset() }
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
    icon: ImageVector,
    direction: Direction,
    onClick: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onClick(direction) },
        modifier = modifier
            .size(70.dp)
            .shadow(elevation = 3.dp, shape = CircleShape)
            .background(Color.White, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$direction Button",
            tint = Color.Black,
            modifier = Modifier.size(42.dp)
        )
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
            contentDescription = "Increase scale",
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
            contentDescription = "Decrease scale",
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
            containerColor = Color.LightGray.copy(alpha = 0.5f),
            contentColor = Color.Black
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
fun ControllerPreview() {
    MaterialTheme {
        TransformControlPanel()
    }
}