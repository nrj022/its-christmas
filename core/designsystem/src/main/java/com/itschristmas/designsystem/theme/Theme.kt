package com.itschristmas.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,            // 버튼, 강조 영역
    onPrimary = SoftBlack,        // 버튼 위의 텍스트

    secondary = Orange,             // 보조 강조 색
    onSecondary = White,      // 그 위의 아이콘/텍스트

    tertiary = Gray,              // 서브 포인트 색
    onTertiary = SoftBlack,       // 그 위의 텍스트

    background = SoftBlack, // 앱 전체 배경
    onBackground = White, // 기본 텍스트 색

    surface = SoftBlack,    // 카드, 다이얼로그 등 배경
    onSurface = White,  // 그 위의 텍스트
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlack,            // 버튼, 강조 영역
    onPrimary = White,        // 버튼 위의 텍스트

    secondary = Orange,             // 보조 강조 색
    onSecondary = White,      // 그 위의 아이콘/텍스트

    tertiary = Gray,              // 서브 포인트 색
    onTertiary = SoftBlack,       // 그 위의 텍스트

    background = White, // 앱 전체 배경
    onBackground = SoftBlack, // 기본 텍스트 색

    surface = White,    // 카드, 다이얼로그 등 배경
    onSurface = SoftBlack,  // 그 위의 텍스트
)

@Composable
fun ItsChristmasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}