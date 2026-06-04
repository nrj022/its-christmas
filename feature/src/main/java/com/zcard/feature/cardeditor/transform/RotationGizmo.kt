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
import kotlin.math.hypot

private const val ROTATION_SENSITIVITY = 0.4f
private const val AXIS_STROKE_SELECTED = 12f
private const val AXIS_STROKE_DEFAULT = 8f
private const val RADIUS_Z_RATIO = 0.9f
private const val RADIUS_XY_RATIO = 0.98f

private val AxisColorX = Color(0XFF85FF74)  // green
private val AxisColorY = Color(0XFFFF7474)  // red
private val AxisColorZ = Color(0XFF6C9AFF)  // blue
private val AxisColorSelected = Color(0XFFFFF941)  // yellow


@Composable
fun RotationGizmo(
    onDragStart: () -> Unit = {},
    onRotate: (Rotation) -> Unit = {},
) {
    // 기즈모에서 현재 선택된 축 상태
    var selectedAxis by remember { mutableStateOf(TransformState.Axis.NONE) }
    var dragDelta by remember { mutableStateOf(Rotation()) }

    fun axisColor(axis: TransformState.Axis): Color {
        if (axis == selectedAxis) return AxisColorSelected

        return when(axis) {
            TransformState.Axis.X -> AxisColorX
            TransformState.Axis.Y -> AxisColorY
            TransformState.Axis.Z -> AxisColorZ
            TransformState.Axis.NONE -> Color.Transparent
        }
    }

    fun axisStroke(axis: TransformState.Axis) = if (axis == selectedAxis) AXIS_STROKE_SELECTED else AXIS_STROKE_DEFAULT

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
                        val firstDown = awaitFirstDown(requireUnconsumed = false)
                        val position = firstDown.position

                        val center = size.width / 2f

                        // Z축은 가장 큰 바깥 원, X와 Y축은 그 안쪽 타원
                        val radiusZ = center * RADIUS_Z_RATIO
                        val radiusXY = radiusZ * RADIUS_XY_RATIO

                        val hitThresholdXY = 60f
                        val hitThresholdZ = 50f // Z축 링의 터치 인식 두께

                        // 중심점으로부터 터치한 곳까지의 거리 (Z축 판별용)
                        val distanceFromCenter = hypot(position.x - center, position.y - center)

                        // [Hit Testing]
                        selectedAxis = when {
                            // 1순위: X축(가로 타원) 근처를 터치했는가?
                            abs(position.y - center) < hitThresholdXY && abs(position.x - center) < radiusXY -> TransformState.Axis.X
                            // 2순위: Y축(세로 타원) 근처를 터치했는가?
                            abs(position.x - center) < hitThresholdXY && abs(position.y - center) < radiusXY -> TransformState.Axis.Y
                            // 3순위: 가장 바깥쪽 Z축 링(테두리) 근처를 터치했는가?
                            abs(distanceFromCenter - radiusZ) < hitThresholdZ -> TransformState.Axis.Z
                            else -> TransformState.Axis.NONE
                        }

                        // 제스처 시작 시점의 rotation 캡처 — drag 람다 안에서 누적용으로 사용
                        dragDelta = Rotation()

                        var lastAngle = 0f
                        if (selectedAxis == TransformState.Axis.Z) lastAngle = atan2(position.y - center, position.x - center)

                        if (selectedAxis != TransformState.Axis.NONE) {
                            onDragStart()
                            drag(firstDown.id) { change ->
                                val dragAmount = change.positionChange()
                                change.consume()

                                val currentPos = change.position

                                when (selectedAxis) {
                                    TransformState.Axis.X -> dragDelta = dragDelta.copy(x = (dragDelta.x - dragAmount.x * ROTATION_SENSITIVITY) % 360)
                                    TransformState.Axis.Y -> dragDelta = dragDelta.copy(y = (dragDelta.y - dragAmount.y * ROTATION_SENSITIVITY) % 360)
                                    TransformState.Axis.Z -> {
                                        // Z축은 직선 드래그가 아니라 '원운동 각도'로 계산
                                        val currentAngle = atan2(currentPos.y - center, currentPos.x - center)
                                        // 라디안 차이를 각도(Degree)로 변환
                                        var angleDiff = Math.toDegrees((currentAngle - lastAngle).toDouble()).toFloat()

                                        // -180 ~ 180도 경계선 처리
                                        if (angleDiff > 180) angleDiff -= 360
                                        if (angleDiff < -180) angleDiff += 360

                                        dragDelta = dragDelta.copy(z = (dragDelta.z + angleDiff) % 360)
                                        lastAngle = currentAngle
                                    }
                                    TransformState.Axis.NONE -> {}
                                }
                                onRotate(dragDelta)
                            }
                        }
                        selectedAxis = TransformState.Axis.NONE
                    }
                }
        ) {
            val center = size.width / 2f
            val radiusZ = center * RADIUS_Z_RATIO
            val radiusXY = radiusZ * RADIUS_XY_RATIO

            // --- X축 띠 그리기 (가로 타원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.X) {
                drawOval(
                    color = axisColor(TransformState.Axis.X),
                    topLeft = Offset(center - radiusXY, center - 20.dp.toPx()),
                    size = Size(radiusXY * 2, 40.dp.toPx()),
                    style = Stroke(width = axisStroke(TransformState.Axis.X))
                )
                if(selectedAxis == TransformState.Axis.X) {
                    drawArc(
                        color = axisColor(TransformState.Axis.X),
                        topLeft = Offset(center - radiusXY, center - 20.dp.toPx()),
                        size = Size(radiusXY * 2, 40.dp.toPx()),
                        startAngle = if(dragDelta.x > 0) 0f else 180f,
                        sweepAngle = dragDelta.x,
                        useCenter = true,
                    )
                }
            }

            // --- Y축 띠 그리기 (세로 타원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.Y) {
                drawOval(
                    color = axisColor(TransformState.Axis.Y),
                    topLeft = Offset(center - 20.dp.toPx(), center - radiusXY),
                    size = Size(40.dp.toPx(), radiusXY * 2),
                    style = Stroke(width = axisStroke(TransformState.Axis.Y))
                )
                if(selectedAxis == TransformState.Axis.Y) {
                    drawArc(
                        color = axisColor(TransformState.Axis.Y),
                        topLeft = Offset(center - 20.dp.toPx(), center - radiusXY),
                        size = Size(40.dp.toPx(), radiusXY * 2),
                        startAngle = if(dragDelta.y > 0) 90f else -90f,
                        sweepAngle = -dragDelta.y,
                        useCenter = true,
                    )
                }
            }

            // --- Z축 띠 그리기 (가장 바깥쪽 정원) ---
            if(selectedAxis == TransformState.Axis.NONE || selectedAxis == TransformState.Axis.Z) {
                drawCircle(
                    color = axisColor(TransformState.Axis.Z),
                    radius = radiusZ,
                    center = Offset(center, center),
                    style = Stroke(width = axisStroke(TransformState.Axis.Z))
                )
                if(selectedAxis == TransformState.Axis.Z) {
                    drawArc(
                        color = axisColor(TransformState.Axis.Z),
                        topLeft = Offset(center - radiusZ, center - radiusZ),
                        size = Size(radiusZ * 2, radiusZ * 2),
                        startAngle = -90f,
                        sweepAngle = dragDelta.z,
                        useCenter = true,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RotationGizmo()
}
