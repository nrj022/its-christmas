package com.example.card.ui.cardeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.card.ui.cardeditor.panels.AssetBrowserPanel

@Composable
fun CardEditorBottomScreen() {
    var myElements by remember { mutableStateOf(emptyList<Int>()) }
    var selectedBackground by remember { mutableIntStateOf(0) }
    var selectedMyElement by remember { mutableStateOf<Int?>(null) }

    AssetBrowserPanel(
        elementItems = (0..7).toList(), // 임시 데이터
        backgroundItems = (0..5).toList(),  // 임시 데이터
        myElements = myElements,
        selectedMyElement = selectedMyElement,
        selectedBackground = selectedBackground,
        onNextClicked = {},
        onElementClicked = { myElements += it },
        onMyElementClicked = {
            selectedMyElement = if (selectedMyElement == it) null else it
        },
        onBackgroundClicked = { selectedBackground = it }
    )
}