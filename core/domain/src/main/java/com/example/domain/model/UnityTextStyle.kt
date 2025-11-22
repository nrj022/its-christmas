package com.example.domain.model

import com.example.domain.enum.UnityFontWeight
import com.example.domain.enum.UnityTextAlign

data class UnityTextStyle(
    val textContent: String = "",
    val fontFamily: String = "Arial",
    val fontSize: Float = 14f,
    val fontWeight: UnityFontWeight = UnityFontWeight.NORMAL,
    val textColor: String = "#000000",
    val textAlign: UnityTextAlign = UnityTextAlign.LEFT,
    val lineSpacing: Float = 18f,
    val letterSpacing: Float = 0f
)