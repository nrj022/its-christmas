package com.zcard.feature.cardeditor.util

import android.content.Context
import com.zcard.feature.R

private const val base62Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

fun getObjectThumbByKey(context: Context, key: String?): Int {
    if (key == null) return R.drawable.thumb_m_placeholder

    val id = context.resources.getIdentifier(key, "drawable", context.packageName)
    return if(id != 0) id else R.drawable.thumb_m_placeholder
}

fun toBase62(num: Long): String {
    if (num == 0L) return "0"

    var n = num
    val result = StringBuilder()

    while (n > 0) {
        val index = (n % 62).toInt()
        result.append(base62Chars[index])
        n /= 62
    }

    return result.reverse().toString()
}