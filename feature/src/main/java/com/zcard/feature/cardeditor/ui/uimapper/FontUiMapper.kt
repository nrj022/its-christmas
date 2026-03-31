package com.zcard.feature.cardeditor.ui.uimapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.zcard.card.R

private val fontResMap: Map<String, Int> = mapOf(
    "PlaywriteUsTradGuides" to R.font.playwriteustradguides_regular,
    "Anton" to R.font.anton_regular,
    "BebasNeue" to R.font.bebasneue_regular,
    "IrishGrover" to R.font.irishgrover_regular,
    "PermanentMarker" to R.font.permanentmarker_regular,
    "PlayfairDisplay" to R.font.playfairdisplay_medium,
    "PlayfairDisplayItalic" to R.font.playfairdisplay_medium_italic,
    "ShadowsIntoLight" to R.font.shadowsintolight_regular,
    "TitanOne" to R.font.titanone_regular
)

@Composable
fun rememberFontFamilies(): Map<String, FontFamily> = remember {
    fontResMap.mapValues { (_, resId) -> FontFamily(Font(resId)) }
}