package com.zcard.feature.cardeditor.textedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.cardeditor.textedit.util.rememberFontFamilies

@Composable
fun TextListPanel(viewModel: TextEditViewModel = hiltViewModel()) {
    val state by viewModel.textEditState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    LaunchedEffect(state.selectedTextTempId) {
        val selectedIndex = state.tempTexts.indexOfFirst { it.tempId == state.selectedTextTempId }
        if (selectedIndex >= 0) scrollState.animateScrollToItem(selectedIndex)
    }

    TextListContent(
        scrollState = scrollState,
        textList = state.tempTexts,
        selectedTextId = state.selectedTextTempId
    ) {
        viewModel.onIntent(TextEditIntent.SelectText(it))
    }
}

@Composable
fun TextListContent(
    scrollState: LazyListState = rememberLazyListState(),
    textList: List<TempText> = emptyList(),
    selectedTextId: Long? = null,
    onClick: (Long) -> Unit = {}
) {
    val fontFamilies = rememberFontFamilies()

    LazyRow(
        modifier = Modifier.padding(horizontal = 10.dp),
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = textList, key = { it.tempId }) { text ->
            TextChip(
                text = text.textElement.attributes.content,
                fontFamily = fontFamilies[text.textElement.attributes.fontFamily.key]
                    ?: FontFamily.Default,
                isSelected = selectedTextId != null && selectedTextId == text.tempId,
                onClick = { onClick(text.tempId) }
            )
        }
    }
}

@Composable
fun TextChip(
    text: String,
    fontFamily: FontFamily,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, if (isSelected) White else SoftBlack),
        colors = CardDefaults.cardColors(containerColor = SoftBlack.copy(0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = fontFamily,
                color = if (isSelected) White else SoftBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                autoSize = TextAutoSize.StepBased(10.sp, 14.sp, stepSize = 1.sp)
            )
        }
    }
}

@Composable
@Preview
fun TextListContentPreview() {
    ZCardTheme {
        TextListContent()
    }
}