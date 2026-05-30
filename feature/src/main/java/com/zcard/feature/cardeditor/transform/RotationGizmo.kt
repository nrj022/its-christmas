package com.zcard.feature.cardeditor.transform

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zcard.designsystem.theme.DimGray
import com.zcard.designsystem.theme.White
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun RotationGizmo() {
    // 3D 카드의 회전 상태
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    var rotationZ by remember { mutableFloatStateOf(0f) }

    // 기즈모에서 현재 선택된 축 상태
    var selectedAxis by remember { mutableStateOf(TransformState.Axis.NONE) }

    // 회전 민감도
    val sensitivity = 0.6f

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = DimGray,
                spotColor = DimGray
            )
            .background(White)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // ==========================================
        // 투명한 기즈모 컨트롤러 영역
        // ==========================================
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val downEvent = awaitFirstDown(requireUnconsumed = false)
                        val position = downEvent.position

                        val cx = size.width / 2f
                        val cy = size.height / 2f

                        // Z축은 가장 큰 바깥 원, X와 Y축은 그 안쪽 타원
                        val radiusZ = (minOf(size.width, size.height) / 2f) * 0.9f
                        val radiusXY = radiusZ * 0.98f

                        val hitThreshold = 60f
                        val hitThresholdZ = 50f // Z축 링의 터치 인식 두께

                        // 중심점으로부터 터치한 곳까지의 거리 (Z축 판별용)
                        val distanceFromCenter = sqrt(
                            (position.x - cx) * (position.x - cx) +
                                    (position.y - cy) * (position.y - cy)
                        )

                        // [Hit Testing]
                        selectedAxis = when {
                            // 1순위: 가장 바깥쪽 Z축 링(테두리) 근처를 터치했는가?
                            abs(distanceFromCenter - radiusZ) < hitThresholdZ -> TransformState.Axis.Z
                            // 2순위: Y축(세로 타원) 근처를 터치했는가?
                            abs(position.x - cx) < hitThreshold && abs(position.y - cy) < radiusXY -> TransformState.Axis.Y
                            // 3순위: X축(가로 타원) 근처를 터치했는가?
                            abs(position.y - cy) < hitThreshold && abs(position.x - cx) < radiusXY -> TransformState.Axis.X
                            else -> TransformState.Axis.NONE
                        }

                        // 터치 시작 시점의 각도 저장 (Z축 회전용)
                        var lastAngle = atan2(position.y - cy, position.x - cx)

                        if (selectedAxis != TransformState.Axis.NONE) {
                            drag(downEvent.id) { change ->
                                val dragAmount = change.positionChange()
                                change.consume()

                                val currentPos = change.position

                                when (selectedAxis) {
                                    TransformState.Axis.Y -> rotationY += dragAmount.y * sensitivity
                                    TransformState.Axis.X -> rotationX += dragAmount.x * sensitivity
                                    TransformState.Axis.Z -> {
                                        // Z축은 직선 드래그가 아니라 '원운동 각도'로 계산
                                        val currentAngle = atan2(currentPos.y - cy, currentPos.x - cx)
                                        // 라디안 차이를 각도(Degree)로 변환
                                        var angleDiff = Math.toDegrees((currentAngle - lastAngle).toDouble()).toFloat()

                                        // -180 ~ 180도 경계선 처리
                                        if (angleDiff > 180) angleDiff -= 360
                                        if (angleDiff < -180) angleDiff += 360

                                        rotationZ += angleDiff
                                        lastAngle = currentAngle
                                    }
                                    TransformState.Axis.NONE -> {}
                                }
                                println("selectedAxis: $selectedAxis")
                                println("dragAmount: ${dragAmount.x}, ${dragAmount.y}")
                                println("rotationX: $rotationX, rotationY: $rotationY, rotationZ: $rotationZ")
                            }
                        }
                        selectedAxis = TransformState.Axis.NONE
                    }
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radiusZ = (minOf(size.width, size.height) / 2f) * 0.9f
            val radiusXY = radiusZ * 0.98f
            val (red, green, blue) = listOf(Color(0XFFFF7474), Color(0XFF85FF74), Color(0XFF6C9AFF))
            val yellow = Color(0XFFFFF941)

            // --- Z축 띠 그리기 (가장 바깥쪽 정원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.Z) {
                val zColor = if (selectedAxis == TransformState.Axis.Z) yellow else blue
                val zStrokeWidth = if (selectedAxis == TransformState.Axis.Z) 12f else 8f

                drawCircle(
                    color = zColor,
                    radius = radiusZ,
                    center = Offset(cx, cy),
                    style = Stroke(width = zStrokeWidth)
                )
            }

            // --- Y축 띠 그리기 (세로 타원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.Y) {
                val yColor = if (selectedAxis == TransformState.Axis.Y) yellow else red
                val yStrokeWidth = if (selectedAxis == TransformState.Axis.Y) 12f else 8f

                drawOval(
                    color = yColor,
                    topLeft = Offset(cx - 20.dp.toPx(), cy - radiusXY),
                    size = Size(40.dp.toPx(), radiusXY * 2),
                    style = Stroke(width = yStrokeWidth)
                )
            }

            // --- X축 띠 그리기 (가로 타원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.X) {
                val xColor = if (selectedAxis == TransformState.Axis.X) yellow else green
                val xStrokeWidth = if (selectedAxis == TransformState.Axis.X) 12f else 8f

                drawOval(
                    color = xColor,
                    topLeft = Offset(cx - radiusXY, cy - 20.dp.toPx()),
                    size = Size(radiusXY * 2, 40.dp.toPx()),
                    style = Stroke(width = xStrokeWidth)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RotationGizmo()
}