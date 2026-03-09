package com.zcard.card.cardeditor.ui.panels

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zcard.domain.model.CardElementWithAssetKeys
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.domain.model.Asset
import com.zcard.card.cardeditor.ui.common.BaseTabs
import com.zcard.card.cardeditor.util.toBase62
import com.zcard.card.cardeditor.model.BaseTabItem
import com.zcard.card.cardeditor.util.getObjectThumbByKey
import com.zcard.designsystem.util.DrawableResProvider.getBgThumbByKey
import com.zcard.card.R

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
    spawnedObjects: List<CardElementWithAssetKeys> = emptyList(),
    selectedSpawnedObject: Long? = null,
    selectedBackground: Long = 1,
    loadingObjectIds: Set<Long> = emptySet(),
    onAddTextClicked: () -> Unit = {},
    onObjectClicked: (Asset) -> Unit = {},
    onSpawnedObjectClicked: (CardElementWithAssetKeys) -> Unit = {},
    onBackgroundClicked: (Long) -> Unit = {},
) {

    var selectedTab by remember { mutableStateOf(AssetBrowserTab.OBJECTS) }

    AssetBrowserPanelContent(
        selectedTab = selectedTab,
        objectItems = objectItems,
        backgroundItems = backgroundItems,
        spawnedObjects = spawnedObjects,
        selectedSpawnedObject = selectedSpawnedObject,
        selectedBackground = selectedBackground,
        loadingObjectIds = loadingObjectIds,
        onTabSelected = { selectedTab = it },
        onAddTextClicked = onAddTextClicked,
        onObjectClicked = onObjectClicked,
        onSpawnedObjectClicked = onSpawnedObjectClicked,
        onBackgroundClicked = onBackgroundClicked
    )
}

@Composable
fun AssetBrowserPanelContent(
    modifier: Modifier = Modifier,
    selectedTab: AssetBrowserTab,
    objectItems: List<Asset>,
    backgroundItems: List<Asset>,
    spawnedObjects: List<CardElementWithAssetKeys>,
    selectedSpawnedObject: Long?,
    selectedBackground: Long,
    loadingObjectIds: Set<Long>,
    onTabSelected: (AssetBrowserTab) -> Unit,
    onObjectClicked: (Asset) -> Unit,
    onSpawnedObjectClicked: (CardElementWithAssetKeys) -> Unit,
    onBackgroundClicked: (Long) -> Unit,
    onAddTextClicked: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().background(White)
    ) {
        // "3D Object", "Background" 토글 및 "NEXT" 버튼이 있는 헤더
        ControlHeader(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            onAddTextClicked = onAddTextClicked
        )

        if (selectedTab == AssetBrowserTab.OBJECTS) {
            if(spawnedObjects.isNotEmpty()) {
                SpawnedObjectRow(
                    elements = spawnedObjects,
                    selectedItemIndex = selectedSpawnedObject,
                    onItemClicked = { onSpawnedObjectClicked(it) },
                    isLoading = { id -> loadingObjectIds.contains(id) }
                )
            }
            ObjectClickableGrid(
                assets = objectItems,
                onItemClicked = { onObjectClicked(it) }
            )
        } else {
            BackgroundSelectableGrid(
                assets = backgroundItems,
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
    onAddTextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // "3D Object", "Background" 토글 버튼 그룹
        BaseTabs(
            tabs = AssetBrowserTab.entries.map { BaseTabItem(it.name, it.resId) },
            selectedTabId = selectedTab.name,
            onTabSelected = { onTabSelected(AssetBrowserTab.valueOf(it)) }
        )

        // "Add Text >" 버튼
        Row(
            modifier = Modifier.clickable(onClick = onAddTextClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.editor_button_add_text),
                style = MaterialTheme.typography.labelSmall,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = SoftBlack
            )
        }
    }
}

@Composable
fun SpawnedObjectRow(
    elements: List<CardElementWithAssetKeys>,
    selectedItemIndex: Long?,
    onItemClicked: (CardElementWithAssetKeys) -> Unit,
    isLoading: (Long) -> Boolean
) {
    val context = LocalContext.current

    Column {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp),
            text = stringResource(R.string.editor_title_spawned_objects_list),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Box(Modifier.size(8.dp)) }
            items(items = elements, key = { it.cardElement.elementId }) { element ->
                val isLoading = isLoading(element.cardElement.elementId)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .border(
                            2.dp,
                            if (element.cardElement.elementId == selectedItemIndex) SoftBlack else Gray,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(!isLoading) { onItemClicked(element) }
                ) {
                    Image(
                        painter = painterResource(id = getObjectThumbByKey(context, element.thumbnailKey)),
                        contentScale = ContentScale.Crop,
                        contentDescription = stringResource(R.string.editor_cd_asset),
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.1f))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = toBase62(element.cardElement.elementId),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.White.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = SoftBlack,
                                strokeWidth = 2.dp,
                            )
                        }
                    }
                }
            }
            item { Box(Modifier.size(8.dp)) }
        }
    }
}

@Composable
fun ObjectClickableGrid(assets: List<Asset>, onItemClicked: (Asset) -> Unit) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = getObjectThumbByKey(context, asset.thumbnailKey)),
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
fun BackgroundSelectableGrid(assets: List<Asset>, selectedItemIndex: Long, onItemClicked: (Long) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = assets, key = { it.assetId }) { asset ->
            Image(
                painter = painterResource(id = getBgThumbByKey(asset.thumbnailKey)),
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
        spawnedObjects = emptyList(),
        objectItems = emptyList(),
        backgroundItems = emptyList()
    )
}
