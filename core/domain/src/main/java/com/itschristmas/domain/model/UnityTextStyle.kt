package com.itschristmas.domain.model

import com.itschristmas.domain.enum.UnityFontWeight
import com.itschristmas.domain.enum.UnityTextAlign

data class UnityTextStyle(
    val textContent: String = "New Text",
    val fontFamily: String = "LiberationSans",
    val fontSize: Float = 14f,
    val fontWeight: UnityFontWeight = UnityFontWeight.NORMAL,
    val textColor: String = "#000000",
    val textAlign: UnityTextAlign = UnityTextAlign.LEFT,
)