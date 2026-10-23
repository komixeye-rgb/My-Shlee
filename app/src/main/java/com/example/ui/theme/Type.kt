package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Custom Font Families
val LexendFontFamily = FontFamily(
    Font(R.font.lexend, FontWeight.Normal),
    Font(R.font.lexend, FontWeight.Medium),
    Font(R.font.lexend, FontWeight.SemiBold),
    Font(R.font.lexend, FontWeight.Bold)
)

val ManropeFontFamily = FontFamily(
    Font(R.font.manrope, FontWeight.Normal),
    Font(R.font.manrope, FontWeight.Medium),
    Font(R.font.manrope, FontWeight.SemiBold),
    Font(R.font.manrope, FontWeight.Bold)
)

// Material 3 Typography system
val Typography = Typography(
    // Headlines (Lexend)
    displayLarge = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = LexendFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    // Body (Manrope)
    bodyLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),

    // Labels (Manrope)
    labelLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.02.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.04.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.05.sp
    )
)
