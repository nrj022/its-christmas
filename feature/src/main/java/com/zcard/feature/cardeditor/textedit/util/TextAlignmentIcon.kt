package com.zcard.feature.cardeditor.textedit.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.ui.graphics.vector.ImageVector
import com.zcard.domain.model.TextAlignment

fun TextAlignment.icon(): ImageVector = when (this) {
    TextAlignment.Right -> Icons.AutoMirrored.Filled.FormatAlignLeft
    TextAlignment.Center -> Icons.Default.FormatAlignCenter
    TextAlignment.Left -> Icons.AutoMirrored.Filled.FormatAlignRight
    TextAlignment.Justify -> Icons.Default.FormatAlignJustify
}