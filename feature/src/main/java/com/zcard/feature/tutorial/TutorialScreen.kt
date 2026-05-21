package com.zcard.feature.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zcard.designsystem.theme.Orange
import com.zcard.designsystem.theme.White
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.R

@Composable
fun TutorialScreen(onStart: () -> Unit = {}) {
    val currentLanguage = Locale.current.language

    val tutorials = if(currentLanguage == "ko") listOf(
        R.drawable.img_tutorial_ko_1,
        R.drawable.img_tutorial_ko_2,
        R.drawable.img_tutorial_ko_3,
    ) else listOf(
        R.drawable.img_tutorial_1,
        R.drawable.img_tutorial_2,
        R.drawable.img_tutorial_3,
    )

    // Pager 상태 관리 (총 페이지 수 지정)
    val pagerState = rememberPagerState(pageCount = { tutorials.size })

    TutorialContent(
        pages = tutorials,
        pagerState = pagerState,
        onStart = onStart
    )
}

@Composable
fun TutorialContent(
    pages: List<Int> = emptyList(),
    pagerState: PagerState = rememberPagerState(pageCount = { 3 }),
    onStart: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 스와이프 가능한 화면 영역
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxHeight().clip(RoundedCornerShape(25.dp)),
                    painter = painterResource(id = pages[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Orange else White

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        // 마지막 페이지에서만 '시작하기' 버튼 노출
        if (pagerState.currentPage == pages.size - 1) {
            Box(
                modifier = Modifier.height(82.dp),
                contentAlignment = Alignment.Center
            ) {
                TextButton (onClick = onStart) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.tutorial_button_start),
                            style = MaterialTheme.typography.labelSmall,
                            color = White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = White
                        )
                    }
                }
            }
        } else {
            // 마지막 페이지가 아닐 때는 자리만 차지하도록 빈 공간 설정
            Spacer(modifier = Modifier.height(82.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TutorialScreenPreview() {
    ZCardTheme {
        TutorialContent()
    }
}