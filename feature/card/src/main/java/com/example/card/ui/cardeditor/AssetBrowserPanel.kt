package com.example.card.ui.cardeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.card.R
import com.example.designsystem.theme.Gray
import com.example.designsystem.theme.SoftBlack
import com.example.designsystem.theme.White

private const val COLUMNS = 3
// 토글 탭 목록 정의
val tabItems = listOf("Elements", "Background")

@Composable
fun AssetBrowserPanel(onNextClicked: () -> Unit = {}) {
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

        if (selectedTab == "Elements") {
            MyAssetList(items = gridItems, selectedItemIndex = 0)
            ClickableGrid(items = gridItems) {}
        } else {
            SelectableGrid(
                items = gridItems,
                // 첫 번째 아이템이 선택된 것처럼 보이게 처리 (임시)
                selectedItemIndex = 0
            )
        }
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
            .padding(horizontal = 20.dp, vertical = 14.dp),
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
            .padding(vertical = 4.dp)
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
fun MyAssetList(items: List<Int>, selectedItemIndex: Int) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp),
            text = "My Elements",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Box(Modifier.size(8.dp)) }

            items(items.size) { index ->
                Image(
                    painter = painterResource(id = R.drawable.img_sample),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Element",
                    modifier = Modifier
                        .size(90.dp)
                        .border(
                            2.dp,
                            if (index == selectedItemIndex) SoftBlack else Gray,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { }
                )
            }
        }
    }
}

@Composable
fun ClickableGrid(items: List<Int>, onItemClicked: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items.size) { index ->
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentScale = ContentScale.Crop,
                contentDescription = "Element",
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Gray, RoundedCornerShape(16.dp))
                    .clickable { onItemClicked(index) }
            )
        }
    }
}

@Composable
fun SelectableGrid(items: List<Int>, selectedItemIndex: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items.size) { index ->
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentScale = ContentScale.Crop,
                contentDescription = "Element",
                modifier = Modifier
                    .aspectRatio(1f) // 1:1 비율 유지
                    .border(
                        2.dp,
                        if (index == selectedItemIndex) SoftBlack else Gray,
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
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
