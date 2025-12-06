package com.itschristmas.domain.model


enum class ColorOption(val rgbaColor: RgbaColor) {
    Black(RgbaColor(r = 0f, g = 0f, b = 0f, a = 1f)),
    White(RgbaColor(r = 1f, g = 1f, b = 1f, a = 1f)),
    Red(RgbaColor(r = 0.7019608f, g = 0f, b = 0f, a = 1f)),
    Green(RgbaColor(r = 0.12156863f, g = 0.5254902f, b = 0f, a = 1f)),
    Blue(RgbaColor(r = 0f, g = 0.02745098f, b = 0.5294118f, a = 1f)),
    Yello(RgbaColor(r = 1f, g = 0.8509804f, b = 0f, a = 1f))
}

data class RgbaColor(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
)