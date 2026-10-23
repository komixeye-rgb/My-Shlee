package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SlateColorScheme = lightColorScheme(
    primary = EmeraldContainer,
    onPrimary = Color.White,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = Color.White,
    secondary = RoyalIndigo,
    onSecondary = Color.White,
    secondaryContainer = EduRoyalBlueLight.copy(alpha = 0.2f),
    onSecondaryContainer = RoyalIndigo,
    tertiary = SunnyAmber,
    onTertiary = Color.White,
    tertiaryContainer = SunnyAmber.copy(alpha = 0.15f),
    onTertiaryContainer = SunnyAmber,
    background = SlateCanvas,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = SlateBorder.copy(alpha = 0.6f),
    error = CoralSalmon,
    onError = Color.White
)

private val DayColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer.copy(alpha = 0.15f),
    onPrimaryContainer = EmeraldPrimary,
    secondary = RoyalIndigo,
    onSecondary = Color.White,
    secondaryContainer = RoyalIndigo.copy(alpha = 0.15f),
    onSecondaryContainer = RoyalIndigo,
    tertiary = SunnyAmber,
    onTertiary = Color.White,
    tertiaryContainer = SunnyAmber.copy(alpha = 0.15f),
    onTertiaryContainer = SunnyAmber,
    background = DayCanvas,
    onBackground = DayTextPrimary,
    surface = DaySurface,
    onSurface = DayTextPrimary,
    surfaceVariant = DaySurfaceVariant,
    onSurfaceVariant = DayTextSecondary,
    outline = DayBorder,
    outlineVariant = DayTextMuted,
    error = CoralSalmon,
    onError = Color.White
)

@Composable
fun SHULEETheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SlateColorScheme else DayColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
