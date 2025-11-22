package com.example.card.ui.cardeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.card.ui.cardeditor.panels.AssetBrowserPanel
import com.example.card.ui.cardeditor.panels.TransformControlPanel

@Composable
fun CardEditorBottomScreen() {
    var myObjects by remember { mutableStateOf(emptyList<Int>()) }
    var selectedBackground by remember { mutableIntStateOf(0) }
    var selectedMyObject by remember { mutableStateOf<Int?>(null) }

    var scale by remember { mutableIntStateOf(1) }

    // TODO: UI State에 따라 각 Panel 표시 상태 변경
    AssetBrowserPanel(
        objectItems = (0..7).toList(), // 임시 데이터
        backgroundItems = (0..5).toList(),  // 임시 데이터
        myObjects = myObjects,
        selectedMyObject = selectedMyObject,
        selectedBackground = selectedBackground,
        onNextClicked = {},
        onObjectClicked = { myObjects += it },
        onMyObjectClicked = {
            selectedMyObject = if (selectedMyObject == it) null else it
        },
        onBackgroundClicked = { selectedBackground = it }
    )

    TransformControlPanel(
        scale = scale,
        onScaleChange = { newScale ->
            if (newScale > 0) { // 0 이하로 내려가지 않도록 예시
                scale = newScale
            }
        },
        onCancel = { },
        onApply = { },
        onDirectionalClick = { direction ->
            println("$direction clicked")
        },
        onReset = { println("Reset clicked") }
    )
}