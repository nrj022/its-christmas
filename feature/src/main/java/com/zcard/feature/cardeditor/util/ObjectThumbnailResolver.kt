package com.zcard.feature.cardeditor.util

import android.content.Context
import com.zcard.feature.R
fun getObjectThumbByKey(context: Context, key: String?): Int {
    if (key == null) return R.drawable.thumb_m_placeholder

    val id = context.resources.getIdentifier(key, "drawable", context.packageName)
    return if(id != 0) id else R.drawable.thumb_m_placeholder
}