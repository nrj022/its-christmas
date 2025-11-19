package com.example.card.ui.cardeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.card.R
import com.example.designsystem.theme.Gray
import com.example.designsystem.theme.SoftBlack
import com.example.designsystem.theme.White

// 토글 탭 목록 정의
val tabItems = listOf("Elements", "Background")

@Composable
fun CardCreationBottomScreen(onNextClicked: () -> Unit = {}) {
    // 현재 선택된 토글 상태를 저장하는 변수 (첫 번째 탭이 기본값)
    var selection by remember { mutableStateOf(tabItems.first()) }

    CardCreationBottomContent(
        selectedTab = selection,
        onTabSelected = { selection = it },
        onNextClicked = onNextClicked
    )
}

@Composable
fun CardCreationBottomContent(
    modifier: Modifier = Modifier,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onNextClicked: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().background(White)
    ) {
        // "Elements", "Background" 토글 및 "NEXT" 버튼이 있는 헤더
        ControlHeader(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onNextClicked = onNextClicked
        )

        // 선택된 토글에 따라 다른 그리드를 표시
        val gridItems = if (selectedTab == "Elements") {
            (1..8).toList() // Elements용 임시 데이터
        } else {
            (1..12).toList() // Background용 임시 데이터
        }
        val columns = if (selectedTab == "Elements") 3 else 4

        SelectableGrid(
            items = gridItems,
            columns = columns,
            // 첫 번째 아이템이 선택된 것처럼 보이게 처리 (임시)
            selectedItemIndex = 0
        )
    }
}

@Composable
fun ControlHeader(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // "Elements", "Background" 토글 버튼 그룹
        ToggleButtons(tabs = tabItems, selectedTab = selectedTab, onTabSelected = onTabSelected)

        // "NEXT >" 버튼
        Row(
            modifier = Modifier.clickable(onClick = onNextClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NEXT",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                modifier = Modifier.size(14.dp),
                tint = SoftBlack
            )
        }
    }
}

@Composable
fun ToggleButtons(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, tabTitle ->
            val isSelected = selectedTab == tabTitle
            val containerColor = if (isSelected) SoftBlack else Gray
            val contentColor = if (isSelected) White else SoftBlack

            Button(
                onClick = { onTabSelected(tabTitle) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
            ) {
                Text(
                    text = tabTitle,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (index < tabs.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


@Composable
fun SelectableGrid(items: List<Int>, columns: Int, selectedItemIndex: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items.size) { index ->
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentDescription = "Element",
                modifier = Modifier
                    .aspectRatio(1f) // 1:1 비율 유지
                    .then(
                        if (index == selectedItemIndex) {
                            Modifier.border(2.dp, SoftBlack, RoundedCornerShape(16.dp))
                        } else {
                            Modifier
                        }
                    ).clip(RoundedCornerShape(16.dp))
                    .clickable { }
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CardCreationScreenPreview() {
    CardCreationBottomContent(
        selectedTab = tabItems.first(),
        onTabSelected = { },
        onNextClicked = { }
    )
}
