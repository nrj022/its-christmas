package com.zcard.feature.cardeditor.textedit.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.zcard.domain.model.TextFontFamily
import com.zcard.feature.R


private val fontResMap: Map<String, Int> = mapOf(
    TextFontFamily.PlaywriteUsTradGuides.key to R.font.playwriteustradguides_regular,
    TextFontFamily.Anton.key to R.font.anton_regular,
    TextFontFamily.BebasNeue.key to R.font.bebasneue_regular,
    TextFontFamily.IrishGrover.key to R.font.irishgrover_regular,
    TextFontFamily.PermanentMarker.key to R.font.permanentmarker_regular,
    TextFontFamily.PlayfairDisplay.key to R.font.playfairdisplay_medium,
    TextFontFamily.PlayfairDisplayItalic.key to R.font.playfairdisplay_medium_italic,
    TextFontFamily.ShadowsIntoLight.key to R.font.shadowsintolight_regular,
    TextFontFamily.TitanOne.key to R.font.titanone_regular
)

@Composable
fun rememberFontFamilies(): Map<String, FontFamily> = remember {
    fontResMap.mapValues { (_, resId) -> FontFamily(Font(resId)) }
}