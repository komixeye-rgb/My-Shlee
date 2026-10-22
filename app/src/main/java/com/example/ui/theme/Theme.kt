package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ShuleeColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color.White,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = Color.White,
    secondary = RoyalIndigo,
    onSecondary = Color.White,
    secondaryContainer = RoyalIndigoContainer,
    onSecondaryContainer = Color.White,
    tertiary = SunnyAmber,
    onTertiary = Color.White,
    tertiaryContainer = SunnyAmberContainer,
    background = SlateCanvas,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = CoralSalmon,
    onError = Color.White
)

@Composable
fun SHULEETheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShuleeColorScheme,
        typography = Typography,
        content = content
    )
}
