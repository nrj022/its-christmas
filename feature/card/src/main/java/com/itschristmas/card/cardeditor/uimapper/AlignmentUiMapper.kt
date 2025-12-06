package com.itschristmas.card.cardeditor.uimapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.ui.graphics.vector.ImageVector
import com.itschristmas.domain.model.TextAlignmentOption

fun TextAlignmentOption.icon(): ImageVector = when (this) {
    TextAlignmentOption.Start -> Icons.AutoMirrored.Filled.FormatAlignLeft
    TextAlignmentOption.Center -> Icons.Default.FormatAlignCenter
    TextAlignmentOption.End -> Icons.AutoMirrored.Filled.FormatAlignRight
    TextAlignmentOption.Justify -> Icons.Default.FormatAlignJustify
}