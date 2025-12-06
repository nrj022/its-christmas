package com.itschristmas.card.cardeditor.panels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itschristmas.card.R
import com.itschristmas.card.cardeditor.common.BaseTabs
import com.itschristmas.card.cardeditor.common.DirectionalController
import com.itschristmas.card.cardeditor.uimapper.icon
import com.itschristmas.card.cardeditor.uimapper.rememberFontFamilies
import com.itschristmas.card.cardeditor.model.BaseTabItems
import com.itschristmas.designsystem.theme.Gray
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import com.itschristmas.designsystem.theme.SoftBlack
import com.itschristmas.designsystem.theme.White
import com.itschristmas.domain.model.ColorOption
import com.itschristmas.domain.model.FontOption
import com.itschristmas.domain.model.TextAlignmentOption

// 토글 탭 목록 정의
enum class TextEditorTab(val resId: Int) {
    STYLE(R.string.editor_title_text_tab_style),
    FONT(R.string.editor_title_text_tab_font),
    POSITION(R.string.editor_title_text_tab_position)
}

@Composable
fun TextEditorPanel(
    text: String = "",
    selectedColor: ColorOption = ColorOption.Black,
    selectedAlignment: TextAlignmentOption = TextAlignmentOption.Start,
    selectedFont: FontOption = FontOption.PlaywriteUsTradGuides,
    fontSize: Float = 24f,
    onTextChange: (String) -> Unit = {},
    onColorSelected: (ColorOption) -> Unit = {},
    onAlignmentSelected: (TextAlignmentOption) -> Unit = {},
    onFontSizeChange: (Float) -> Unit = {},
    onFontSelected: (FontOption) -> Unit = {},
    onApply: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(TextEditorTab.STYLE) }

    EditorTabContent(
        text = text,
        selectedTab = selectedTab,
        selectedColor = selectedColor,
        selectedAlignment = selectedAlignment,
        selectedFont = selectedFont,
        fontSize = fontSize,
        onTextChange = onTextChange,
        onTabSelected = { selectedTab = it },
        onColorSelected = onColorSelected,
        onAlignmentSelected = onAlignmentSelected,
        onFontSizeChange = onFontSizeChange,
        onFontSelected = onFontSelected,
        onApply = onApply,
        onBack = onBack
    )
}

@Composable
private fun EditorTabContent(
    text: String,
    selectedTab: TextEditorTab,
    selectedColor: ColorOption,
    selectedAlignment: TextAlignmentOption,
    selectedFont: FontOption,
    fontSize: Float,
    onTextChange: (String) -> Unit,
    onTabSelected: (TextEditorTab) -> Unit,
    onColorSelected: (ColorOption) -> Unit,
    onAlignmentSelected: (TextAlignmentOption) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontSelected: (FontOption) -> Unit,
    onApply: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(White)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        // 텍스트 입력, 적용, 뒤로 가기
        EditorHeader(
            text = text,
            onBack = onBack,
            onApply = onApply,
            onTextChange = onTextChange
        )
        Spacer(Modifier.height(18.dp))

        // "Style", "font", "Position" 탭
        BaseTabs(
            tabs = TextEditorTab.entries.map { BaseTabItems(it.name, it.resId) },
            selectedTabId = selectedTab.name,
            onTabSelected = { onTabSelected(TextEditorTab.valueOf(it)) }
        )
        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            TextEditorTab.STYLE -> {
                StyleOptions(
                    selectedColor = selectedColor,
                    selectedAlignment = selectedAlignment,
                    fontSize = fontSize,
                    onColorSelected = onColorSelected,
                    onAlignmentSelected = onAlignmentSelected,
                    onFontSizeChange = onFontSizeChange
                )
            }
            TextEditorTab.FONT -> {
                FontOptions(selectedFont = selectedFont, onFontSelected = onFontSelected)
            }
            TextEditorTab.POSITION -> {
                PositionOptions()
            }
        }
    }
}

@Composable
private fun EditorHeader(
    text: String,
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
                )
            }
        }
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
                            style = MaterialTheme.typography.labelSmall
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
    selectedColor: ColorOption,
    selectedAlignment: TextAlignmentOption,
    fontSize: Float,
    onColorSelected: (ColorOption) -> Unit,
    onAlignmentSelected: (TextAlignmentOption) -> Unit,
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
    selectedAlignment: TextAlignmentOption,
    onAlignmentSelected: (TextAlignmentOption) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TextAlignmentOption.entries.forEach { align ->
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
private fun ColorOptions(selectedColor: ColorOption, onColorSelected: (ColorOption) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(ColorOption.entries, key = { it.name }) { color ->
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
private fun FontOptions(selectedFont: FontOption, onFontSelected: (FontOption) -> Unit) {
    val fontFamilies = rememberFontFamilies()

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(FontOption.entries, key = { it.key }) { font ->
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
private fun FontChip(font: FontOption, isSelected: Boolean, fontFamily: FontFamily?, onClick: (FontOption) -> Unit) {
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
private fun PositionOptions() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        DirectionalController(
            onClick = {},
            onCameraReset = {}
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun TextEditorPanelPreview() {
    ItsChristmasTheme {
        TextEditorPanel()
    }
}
