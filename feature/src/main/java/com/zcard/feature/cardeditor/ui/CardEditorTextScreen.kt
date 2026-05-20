package com.zcard.feature.cardeditor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.feature.cardeditor.CardEditorIntent
import com.zcard.feature.cardeditor.CardEditorViewModel
import com.zcard.feature.cardeditor.ui.panels.TextListPanel

@Composable
fun CardEditorTextScreen(viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    TextListPanel(
        textList = state.tempTextList,
        selectedTextId = state.selectedTextTempId ?: 0
    ) {
        viewModel.onIntent(CardEditorIntent.SelectText(it))
    }
}