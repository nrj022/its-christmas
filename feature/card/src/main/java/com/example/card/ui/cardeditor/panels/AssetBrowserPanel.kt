package com.example.card.ui.cardeditor.panels

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.card.R
import com.example.designsystem.theme.Gray
import com.example.designsystem.theme.SoftBlack
import com.example.designsystem.theme.White
import com.example.domain.model.Asset
import com.example.domain.model.CardElement

private const val COLUMNS = 3

// 토글 탭 목록 정의
enum class AssetBrowserTab(val resId: Int) {
    OBJECTS(R.string.editor_title_asset_tab_objects),
    BACKGROUND(R.string.editor_title_asset_tab_background)
}

@Composable
fun AssetBrowserPanel(
    objectItems: List<Asset> = emptyList(),
    backgroundItems: List<Asset> = emptyList(),
    myObjects: List<CardElement> = emptyList(),
    selectedMyObject: Long? = null,
    selectedBackground: Long? = null,
    onNextClicked: () -> Unit = {},
    onObjectClicked: (Asset) -> Unit = {},
    onMyObjectClicked: (Long) -> Unit = {},
    onBackgroundClicked: (Long) -> Unit = {},
) {

    var selectedTab by remember { mutableStateOf(AssetBrowserTab.OBJECTS) }

    AssetBrowserPanelContent(
        selectedTab = selectedTab,
        objectItems = objectItems,
        backgroundItems = backgroundItems,
        myObjects = myObjects,
        selectedMyObject = selectedMyObject,
        selectedBackground = selectedBackground,
        onTabSelected = { selectedTab = it },
        onNextClicked = onNextClicked,
        onObjectClicked = onObjectClicked,
        onMyObjectClicked = onMyObjectClicked,
        onBackgroundClicked = onBackgroundClicked
    )
}

@Composable
fun AssetBrowserPanelContent(
    modifier: Modifier = Modifier,
    selectedTab: AssetBrowserTab,
    objectItems: List<Asset>,
    backgroundItems: List<Asset>,
    myObjects: List<CardElement>,
    selectedMyObject: Long?,
    selectedBackground: Long?,
    onTabSelected: (AssetBrowserTab) -> Unit,
    onObjectClicked: (Asset) -> Unit,
    onMyObjectClicked: (Long) -> Unit,
    onBackgroundClicked: (Long) -> Unit,
    onNextClicked: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().background(White)
    ) {
        // "3D Object", "Background" 토글 및 "NEXT" 버튼이 있는 헤더
        ControlHeader(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onNextClicked = onNextClicked
        )

        if (selectedTab == AssetBrowserTab.OBJECTS) {
            if(myObjects.isNotEmpty()) {
                MyObjectList(
                    elements = myObjects,
                    selectedItemIndex = selectedMyObject,
                    onItemClicked = { onMyObjectClicked(it) }
                )
            }
            ClickableGrid(
                assets = objectItems,
                onItemClicked = { onObjectClicked(it) }
            )
        } else {
            SelectableGrid(
                assets = backgroundItems,
                // 첫 번째 아이템이 선택된 것처럼 보이게 처리 (임시)
                selectedItemIndex = selectedBackground,
                onItemClicked = { onBackgroundClicked(it) }
            )
        }
    }
}

@Composable
fun ControlHeader(
    selectedTab: AssetBrowserTab,
    onTabSelected: (AssetBrowserTab) -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // "3D Object", "Background" 토글 버튼 그룹
        ToggleButtons(selectedTab = selectedTab, onTabSelected = onTabSelected)

        // "NEXT >" 버튼
        Row(
            modifier = Modifier.clickable(onClick = onNextClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.editor_label_next_button),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 14.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.editor_cd_next_button),
                modifier = Modifier.size(14.dp),
                tint = SoftBlack
            )
        }
    }
}

@Composable
fun ToggleButtons(
    selectedTab: AssetBrowserTab,
    onTabSelected: (AssetBrowserTab) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .padding(vertical = 4.dp)
    ) {
        AssetBrowserTab.entries.forEachIndexed { index, item ->
            val isSelected = selectedTab == item
            val containerColor = if (isSelected) SoftBlack else Gray
            val contentColor = if (isSelected) White else SoftBlack

            Button(
                onClick = { onTabSelected(item) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
            ) {
                Text(
                    text = stringResource(item.resId),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (index < AssetBrowserTab.entries.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun MyObjectList(elements: List<CardElement>, selectedItemIndex: Long?, onItemClicked: (Long) -> Unit) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp),
            text = stringResource(R.string.editor_title_my_objects_list),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Box(Modifier.size(8.dp)) }

            items(items = elements, key = { it.elementId } ) { element ->
                Image(
                    painter = painterResource(id = R.drawable.img_sample),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(R.string.editor_cd_asset),
                    modifier = Modifier
                        .size(90.dp)
                        .border(
                            2.dp,
                            if (element.elementId == selectedItemIndex) SoftBlack else Gray,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onItemClicked(element.elementId) }
                )
            }
        }
    }
}

@Composable
fun ClickableGrid(assets: List<Asset>, onItemClicked: (Asset) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.editor_cd_asset),
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Gray, RoundedCornerShape(16.dp))
                    .clickable { onItemClicked(asset) }
            )
        }
    }
}

@Composable
fun SelectableGrid(assets: List<Asset>, selectedItemIndex: Long?, onItemClicked: (Long) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.editor_cd_asset),
                modifier = Modifier
                    .aspectRatio(1f) // 1:1 비율 유지
                    .border(
                        2.dp,
                        if (asset.assetId == selectedItemIndex) SoftBlack else Gray,
                        RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onItemClicked(asset.assetId) }
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CardCreationScreenPreview() {
    AssetBrowserPanel(
        myObjects = emptyList(),
        objectItems = emptyList(),
        backgroundItems = emptyList()
    )
}
