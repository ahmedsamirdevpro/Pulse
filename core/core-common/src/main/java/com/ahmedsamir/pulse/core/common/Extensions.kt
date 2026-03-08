package com.ahmedsamir.pulse.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        diff < 604_800_000 -> "${diff / 86_400_000}d"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(this))
    }
}

fun Int.toFormattedCount(): String {
    return when {
        this < 1_000 -> this.toString()
        this < 1_000_000 -> String.format("%.1fK", this / 1_000.0)
        else -> String.format("%.1fM", this / 1_000_000.0)
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length >= 8
}

fun String.isValidUsername(): Boolean {
    return this.length >= 3 && this.matches(Regex("^[a-zA-Z0-9_.]+$"))
}