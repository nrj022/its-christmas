package com.zcard.card.cardeditor.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zcard.card.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun BouncingLogoLoadingOverlay(
    text: String,
    modifier: Modifier = Modifier,
    logoSize: Dp = 48.dp,
    backgroundColor: Color = Color.Black.copy(alpha = 0.7f),
) {
    val transition = rememberInfiniteTransition(label = "loading")

    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                // 더블 바운스 리듬: 크게 한번 -> 작게 한번 -> 잠깐 정지
                0f at 0 using FastOutSlowInEasing
                (-100f) at 264 using FastOutSlowInEasing
                0f at 552 using FastOutSlowInEasing
                (-24f) at 756 using FastOutSlowInEasing
                0f at 948 using FastOutSlowInEasing
                0f at 1200 // pause
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    val scale by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                // offsetY 리듬과 동기화: 작게 -> 크게, 착지 시 살짝 눌림
                1.0f at 0 using FastOutSlowInEasing
                0.96f at 264 using FastOutSlowInEasing
                0.985f at 552 using FastOutSlowInEasing
                1.03f at 756 using FastOutSlowInEasing
                0.99f at 948 using FastOutSlowInEasing
                1.0f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    val rotationZ by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                // 첫 번째 큰 점프(0~552ms)에만 1회전, 이후 구간은 고정
                0f at 0 using FastOutSlowInEasing
                360f at 552 using FastOutSlowInEasing
                360f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationZ"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo_z),
                contentDescription = null,
                modifier = Modifier
                    .size(logoSize)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .rotate(rotationZ)
                    .scale(scale)
            )

            LoadingTextWithCyclingDots(
                text = text,
                modifier = Modifier.offset(y = 10.dp),
            )
        }
    }
}

@Composable
private fun LoadingTextWithCyclingDots(
    text: String,
    modifier: Modifier = Modifier,
    maxDots: Int = 3,
    stepMs: Long = 300L,
) {
    val dots = remember { mutableIntStateOf(0) }

    LaunchedEffect(maxDots, stepMs) {
        while (true) {
            delay(stepMs)

            val next = dots.intValue + 1
            dots.intValue = if(next > maxDots) 0 else next
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
        )
        Text(
            text = ".".repeat(dots.intValue),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
        )
    }
}

