package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EducationColorScheme = lightColorScheme(
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
    background = EduCanvas,
    onBackground = EduTextPrimary,
    surface = EduSurface,
    onSurface = EduTextPrimary,
    surfaceVariant = EduSurfaceVariant,
    onSurfaceVariant = EduTextSecondary,
    outline = EduBorder,
    outlineVariant = EduBorder.copy(alpha = 0.6f),
    error = EduCoralRose,
    onError = Color.White
)

@Composable
fun SHULEETheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EducationColorScheme,
        typography = Typography,
        content = content
    )
}
