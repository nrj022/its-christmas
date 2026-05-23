package com.zcard.feature.cardeditor.textedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zcard.designsystem.theme.Gray
import com.zcard.designsystem.theme.SoftBlack
import com.zcard.designsystem.theme.White
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.domain.model.TextAlignment
import com.zcard.domain.model.TextColor
import com.zcard.domain.model.TextElement
import com.zcard.domain.model.TextFontFamily
import com.zcard.feature.R
import com.zcard.feature.cardeditor.model.BaseTabItem
import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.model.Direction
import com.zcard.feature.cardeditor.ui.common.BaseTabs
import com.zcard.feature.cardeditor.ui.common.DirectionalController
import com.zcard.feature.cardeditor.ui.dialog.UnsavedChangesDialog
import com.zcard.feature.cardeditor.ui.uimapper.icon
import com.zcard.feature.cardeditor.ui.uimapper.rememberFontFamilies

@Composable
fun TextEditScreen(viewModel: TextEditViewModel = hiltViewModel()) {
    val state by viewModel.textEditState.collectAsStateWithLifecycle()
    val tempText = state.selectedText
    if(tempText == null) {
        viewModel.onIntent(TextEditIntent.MissingTextSelection)
    }

    TextEditContent(
        textElement = tempText?.textElement,
        onTextChange = { viewModel.onIntent(TextEditIntent.ChangeTextContent(it)) },
        onAlignmentSelected = { viewModel.onIntent(TextEditIntent.SelectAlignment(it)) },
        onColorSelected = { viewModel.onIntent(TextEditIntent.SelectColor(it)) },
        onFontSizeChange = { viewModel.onIntent(TextEditIntent.ChangeFontSize(it)) },
        onFontSelected = { viewModel.onIntent(TextEditIntent.SelectFont(it)) },
        onPositionChange = { viewModel.onIntent(TextEditIntent.MoveText(it)) },
        onApply = { viewModel.onIntent(TextEditIntent.SaveChanges) },
        onBack = { viewModel.onIntent(TextEditIntent.Exit) }
    )

    if(state.showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onApplyChanges = { viewModel.onIntent(TextEditIntent.SaveAndExit) },
            onDiscardChanges = { viewModel.onIntent(TextEditIntent.DiscardAndExit) },
            onDismiss = { viewModel.onIntent(TextEditIntent.DismissDialog) }
        )
    }
}

// 토글 탭 목록 정의
enum class TextEditorTab(val resId: Int) {
    STYLE(R.string.editor_title_text_tab_style),
    FONT(R.string.editor_title_text_tab_font),
    POSITION(R.string.editor_title_text_tab_position)
}

@Composable
fun TextEditContent(
    textElement: TextElement? = null,
    onTextChange: (String) -> Unit = {},
    onColorSelected: (TextColor) -> Unit = {},
    onAlignmentSelected: (TextAlignment) -> Unit = {},
    onFontSizeChange: (Float) -> Unit = {},
    onFontSelected: (TextFontFamily) -> Unit = {},
    onPositionChange: (Direction) -> Unit = { },
    onApply: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(TextEditorTab.STYLE) }

    EditorTabContent(
        textElement = textElement,
        selectedTab = selectedTab,
        onTextChange = onTextChange,
        onTabSelected = { selectedTab = it },
        onColorSelected = onColorSelected,
        onAlignmentSelected = onAlignmentSelected,
        onFontSizeChange = onFontSizeChange,
        onFontSelected = onFontSelected,
        onPositionChange = onPositionChange,
        onApply = onApply,
        onBack = onBack
    )
}

@Composable
private fun EditorTabContent(
    textElement: TextElement?,
    selectedTab: TextEditorTab,
    onTextChange: (String) -> Unit,
    onTabSelected: (TextEditorTab) -> Unit,
    onColorSelected: (TextColor) -> Unit,
    onAlignmentSelected: (TextAlignment) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontSelected: (TextFontFamily) -> Unit,
    onPositionChange: (Direction) -> Unit,
    onApply: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(White)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        // 텍스트 입력, 적용, 뒤로 가기
        EditorHeader(
            text = textElement?.attributes?.content ?: "",
            hasText = textElement != null,
            onBack = onBack,
            onApply = onApply,
            onTextChange = onTextChange
        )

        if (textElement == null) {
            EmptyEditorContent()
            return@Column
        }

        Spacer(Modifier.height(18.dp))

        // "Style", "font", "Position" 탭
        BaseTabs(
            tabs = TextEditorTab.entries.map { BaseTabItem(it.name, it.resId) },
            selectedTabId = selectedTab.name,
            onTabSelected = { onTabSelected(TextEditorTab.valueOf(it)) }
        )
        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            TextEditorTab.STYLE -> {
                StyleOptions(
                    selectedColor = textElement.attributes.textColor,
                    selectedAlignment = textElement.attributes.alignment,
                    fontSize = textElement.attributes.fontSize,
                    onColorSelected = onColorSelected,
                    onAlignmentSelected = onAlignmentSelected,
                    onFontSizeChange = onFontSizeChange
                )
            }
            TextEditorTab.FONT -> {
                FontOptions(selectedFont = textElement.attributes.fontFamily, onFontSelected = onFontSelected)
            }
            TextEditorTab.POSITION -> {
                PositionOptions(onPositionChange = onPositionChange)
            }
        }
    }
}

@Composable
private fun EditorHeader(
    text: String,
    hasText: Boolean,
    onBack: () -> Unit,
    onApply: () -> Unit,
    onTextChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_cd_back_button),
                    modifier = Modifier.size(14.dp),
                    tint = SoftBlack
                )
                Text(
                    text = stringResource(R.string.editor_button_back),
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftBlack
                )
            }
        }

        if(!hasText) return@Row

        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = text,
            textStyle = MaterialTheme.typography.labelSmall,
            onValueChange = onTextChange,
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .background(Gray)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.editor_placeholder_text),
                            style = MaterialTheme.typography.labelSmall,
                            color = White
                        )
                    }
                    innerTextField()
                }
            }
        )
        TextButton(onClick = onApply) {
            Text(
                text = stringResource(R.string.common_button_apply),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun StyleOptions(
    selectedColor: TextColor,
    selectedAlignment: TextAlignment,
    fontSize: Float,
    onColorSelected: (TextColor) -> Unit,
    onAlignmentSelected: (TextAlignment) -> Unit,
    onFontSizeChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AlignmentOptions(
            selectedAlignment = selectedAlignment,
            onAlignmentSelected = onAlignmentSelected
        )
        ColorOptions(
            selectedColor = selectedColor,
            onColorSelected = onColorSelected
        )
        FontSizeSlider(
            fontSize = fontSize,
            onFontSizeChange = onFontSizeChange
        )
    }
}

@Composable
private fun AlignmentOptions(
    selectedAlignment: TextAlignment,
    onAlignmentSelected: (TextAlignment) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TextAlignment.entries.forEach { align ->
            IconButton(onClick = { onAlignmentSelected(align) }) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = align.icon(),
                    contentDescription = stringResource(R.string.editor_cd_align_icon, align.name),
                    tint = if (selectedAlignment == align) SoftBlack else Gray
                )
            }
        }
    }
}

@Composable
private fun ColorOptions(selectedColor: TextColor, onColorSelected: (TextColor) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(TextColor.entries, key = { it.name }) { color ->
            Box(
                modifier = Modifier
                    .semantics { contentDescription = color.name }
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(red = color.rgbaColor.r, green = color.rgbaColor.g, blue = color.rgbaColor.b))
                    .border(
                        width = 2.dp,
                        color = if (selectedColor == color) SoftBlack else Gray,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun FontSizeSlider(fontSize: Float, onFontSizeChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${fontSize.toInt()}px",
            style = MaterialTheme.typography.labelSmall,
        )
        Slider(
            modifier = Modifier.weight(1f),
            value = fontSize,
            onValueChange = onFontSizeChange,
            valueRange = 12f..48f,
            steps = 35,
            colors = SliderDefaults.colors(
                thumbColor = SoftBlack,
                activeTrackColor = SoftBlack,
                inactiveTrackColor = Gray,
                activeTickColor = SoftBlack,
                inactiveTickColor = Gray,
            )
        )
    }
}

@Composable
private fun FontOptions(selectedFont: TextFontFamily, onFontSelected: (TextFontFamily) -> Unit) {
    val fontFamilies = rememberFontFamilies()

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TextFontFamily.entries, key = { it.key }) { font ->
            FontChip(
                font = font,
                isSelected = selectedFont == font,
                fontFamily = fontFamilies[font.key],
                onClick = onFontSelected
            )
        }
    }
}

@Composable
private fun FontChip(font: TextFontFamily, isSelected: Boolean, fontFamily: FontFamily?, onClick: (TextFontFamily) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick(font) },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, if (isSelected) SoftBlack else Gray),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = font.name,
                maxLines = 1,
                fontFamily = fontFamily ?: FontFamily.Default,
                overflow = TextOverflow.Ellipsis,
                autoSize = TextAutoSize.StepBased(8.sp, 14.sp, stepSize = 1.sp)
            )
        }
    }
}

@Composable
private fun PositionOptions(onPositionChange: (Direction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        DirectionalController(
            onClick = onPositionChange,
        )
    }
}

@Composable
private fun EmptyEditorContent() {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.editor_content_empty_text),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun TextEditorPanelPreview() {
    ZCardTheme {
        TextEditContent()
    }
}
