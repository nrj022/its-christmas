package com.itschristmas.card.cardeditor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itschristmas.card.cardeditor.panels.TextListPanel

@Composable
fun CardEditorTextScreen(viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.cardEditorState.collectAsStateWithLifecycle()

    TextListPanel(
        textList = state.tempTextList,
        selectedTextId = state.selectedTextTempId ?: 0
    ) {
        viewModel.onIntent(CardEditorIntent.TextClicked(it))
    }
}