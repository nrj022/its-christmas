package com.itschristmas.card.cardeditor.common

import android.content.Context
import com.itschristmas.card.R

fun getDrawableIdByKey(context: Context, key: String?): Int {
    if (key == null) return R.drawable.img_placeholder

    val id = context.resources.getIdentifier(key, "drawable", context.packageName)
    return if(id != 0) id else R.drawable.img_placeholder
}