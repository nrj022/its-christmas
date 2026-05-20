package com.zcard.feature.home.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatRelativeTime(millis: Long): String {
    val now = Instant.now()
    val then = Instant.ofEpochMilli(millis)
    val duration = Duration.between(then, now)

    return when {
        duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
        duration.toHours() < 24 -> "${duration.toHours()} hours ago"
        duration.toDays() < 30 -> "${duration.toDays()} days ago"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            then.atZone(ZoneId.systemDefault()).format(formatter)
        }
    }
}

fun formatDateTime(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val dateTime = Instant.ofEpochMilli(millis)
    return dateTime.atZone(ZoneId.systemDefault()).format(formatter)
}