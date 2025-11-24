package com.example.card.ui.cardeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.card.ui.cardeditor.panels.AssetBrowserPanel
import com.example.card.ui.cardeditor.panels.TransformControlPanel

@Composable
fun CardEditorBottomScreen(cardId: Long, viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    var scale by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(CardEditorIntent.Init(cardId))
    }

    // TODO: UI State에 따라 각 Panel 표시 상태 변경
    AssetBrowserPanel(
        objectItems = state.objects, // 임시 데이터
        backgroundItems = state.backgrounds,  // 임시 데이터
        myObjects = state.myObjects,
        selectedMyObject = state.selectedMyObject,
        selectedBackground = state.selectedBackground,
        onNextClicked = {},
        onObjectClicked = { viewModel.onIntent(CardEditorIntent.ObjectClicked(cardId, it)) },
        onMyObjectClicked = { viewModel.onIntent(CardEditorIntent.MyObjectClicked(it)) },
        onBackgroundClicked = { viewModel.onIntent(CardEditorIntent.BackgroundClicked(it)) }
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