package com.zcard.designsystem.util

import com.zcard.designsystem.R

object DrawableResProvider {

    private val drawableMap = mapOf(
        "placeholder" to R.drawable.thumb_bg_placeholder,
        "thumb_bg_001" to R.drawable.thumb_bg_001,
        "thumb_bg_002" to R.drawable.thumb_bg_002,
        "thumb_bg_003" to R.drawable.thumb_bg_003,
        "thumb_bg_004" to R.drawable.thumb_bg_004,
        "thumb_bg_005" to R.drawable.thumb_bg_005,
        "thumb_bg_006" to R.drawable.thumb_bg_006,
    )

    fun getBgThumbByKey(key: String?): Int {
        if (key.isNullOrBlank()) return R.drawable.thumb_bg_placeholder
        return drawableMap[key] ?: R.drawable.thumb_bg_placeholder
    }
}