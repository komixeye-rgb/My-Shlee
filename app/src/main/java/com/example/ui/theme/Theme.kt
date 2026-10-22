package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ShuleeColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color.White,
    primaryContainer = SlateSurfaceLight,
    onPrimaryContainer = TextPrimary,
    secondary = EmeraldDistinct,
    onSecondary = Color.White,
    secondaryContainer = EmeraldDistinct.copy(alpha = 0.2f),
    tertiary = FlameOrange,
    onTertiary = Color.White,
    background = SlateCanvas,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder
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
