package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SlateColorScheme = lightColorScheme(
    primary = EduRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = EduRoyalBlueLight.copy(alpha = 0.12f),
    onPrimaryContainer = EduRoyalBlue,
    secondary = EduTealGreen,
    onSecondary = Color.White,
    secondaryContainer = EduTealGreen.copy(alpha = 0.12f),
    onSecondaryContainer = EduTealDark,
    tertiary = EduCoralRose,
    onTertiary = Color.White,
    tertiaryContainer = EduCoralRose.copy(alpha = 0.12f),
    onTertiaryContainer = EduCoralDark,
    background = SlateCanvas,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = SlateBorder.copy(alpha = 0.6f),
    error = EduCoralRose,
    onError = Color.White
)

private val DayColorScheme = lightColorScheme(
    primary = EduRoyalBlueDark, // Deeper blue for higher contrast in sunlight
    onPrimary = Color.White,
    primaryContainer = EduRoyalBlue.copy(alpha = 0.12f),
    onPrimaryContainer = EduRoyalBlueDark,
    secondary = EduTealDark,
    onSecondary = Color.White,
    secondaryContainer = EduTealLight.copy(alpha = 0.15f),
    onSecondaryContainer = EduTealDark,
    tertiary = EduCoralDark,
    onTertiary = Color.White,
    tertiaryContainer = EduCoralRose.copy(alpha = 0.1f),
    onTertiaryContainer = EduCoralDark,
    background = DayCanvas,
    onBackground = DayTextPrimary,
    surface = DaySurface,
    onSurface = DayTextPrimary,
    surfaceVariant = DaySurfaceVariant,
    onSurfaceVariant = DayTextSecondary,
    outline = DayBorder,
    outlineVariant = DayBorder.copy(alpha = 0.8f),
    error = EduCoralDark,
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
