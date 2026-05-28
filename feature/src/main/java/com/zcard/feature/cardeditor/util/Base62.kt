package com.zcard.feature.cardeditor.util

private const val base62Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

fun Long.toBase62(): String {
    if (this == 0L) return "0"

    var n = this
    val result = StringBuilder()

    while (n > 0) {
        val index = (n % 62).toInt()
        result.append(base62Chars[index])
        n /= 62
    }

    return result.reverse().toString()
}