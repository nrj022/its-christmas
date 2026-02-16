package com.itschristmas.card.cardeditor.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itschristmas.card.cardeditor.ui.common.DirectionalController
import com.itschristmas.card.cardeditor.model.Direction

/**
 * 이미지에 표시된 컨트롤러 전체 UI
 * @param scale 현재 수량 (가운데 숫자)
 * @param onScaleChange 수량 변경 시 호출되는 콜백 (+, - 버튼)
 * @param onDirectionalClick 방향키 클릭 시 호출되는 콜백
 * @param onCameraReset 가운데 원(원래 상태로) 클릭 시 호출되는 콜백
 * @param onCancel 'Cancel' 버튼 클릭 시 호출되는 콜백
 * @param onApply 'Apply' 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun TransformControlPanel(
    scale: Int = 1,
    onScaleChange: (Int) -> Unit = {},
    onDirectionalClick: (Direction) -> Unit = {},
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
            modifier = Modifier.padding(top = 10.dp, bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp) // 방향키와 수량 조절기 사이 간격
        ) {
            // 2-1. 방향키 컨트롤러
            DirectionalController(onClick = onDirectionalClick)

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