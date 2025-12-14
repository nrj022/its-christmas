package com.itschristmas.data.mapper

import com.itschristmas.data.dto.ColorDto
import com.itschristmas.domain.model.RgbaColor

fun RgbaColor.toDto(): ColorDto =
    ColorDto(
        r = r,
        g = g,
        b = b,
        a = a
    )