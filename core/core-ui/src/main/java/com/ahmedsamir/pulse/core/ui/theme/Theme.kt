package com.ahmedsamir.pulse.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PulseDarkColorScheme = darkColorScheme(
    primary = PulseBlue,
    onPrimary = PulseWhite,
    background = PulseDarkBg,
    onBackground = PulseTextPrimary,
    surface = PulseDarkSurface,
    onSurface = PulseTextPrimary,
    surfaceVariant = PulseDarkCard,
    onSurfaceVariant = PulseTextSecondary,
    outline = PulseBorder,
    error = PulseRed,
    onError = PulseWhite
)

@Composable
fun PulseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PulseDarkColorScheme,
        typography = PulseTypography,
        content = content
    )
}