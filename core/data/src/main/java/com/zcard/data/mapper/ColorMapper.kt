package com.zcard.data.mapper

import com.zcard.data.dto.ColorDto
import com.zcard.domain.model.RgbaColor

fun RgbaColor.toDto(): ColorDto =
    ColorDto(
        r = r,
        g = g,
        b = b,
        a = a
    )