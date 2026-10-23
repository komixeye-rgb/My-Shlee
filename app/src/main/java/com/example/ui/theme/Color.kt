package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Modern Vibrant Education Design System (based on Education App UI Mockup)
// Primary & Accent Brand Brand Colors (Shared across themes)
val EduRoyalBlue = Color(0xFF4F46E5)       // Signature Education Royal Indigo for primary CTAs & headers
val EduRoyalBlueDark = Color(0xFF3730A3)
val EduRoyalBlueLight = Color(0xFF6366F1)

val EduTealGreen = Color(0xFF10B981)       // Education Mint/Teal for success, active tab & graduation cap
val EduTealDark = Color(0xFF047857)
val EduTealLight = Color(0xFF34D399)

val EduCoralRose = Color(0xFFFF4D6D)       // High-impact Coral Rose for video lessons, alerts & play buttons
val EduCoralDark = Color(0xFFE11D48)

val EduSunnyAmber = Color(0xFFF59E0B)      // Warm Sunshine Amber for streak badges, stars & highlights
val EduSunnyAmberContainer = Color(0xFFD97706)

val EduSkyCyan = Color(0xFF0EA5E9)

// --- DAY MODE (High Contrast Light Palette) ---
val DayCanvas = Color(0xFFF8FAFC)          // Crisp off-white canvas
val DaySurface = Color(0xFFFFFFFF)         // Pure white cards
val DaySurfaceVariant = Color(0xFFF1F5F9)  // Light gray variant
val DayBorder = Color(0xFFE2E8F0)          // Subtle border
val DayTextPrimary = Color(0xFF0F172A)     // Deep navy for sharp text
val DayTextSecondary = Color(0xFF475569)   // Medium slate
val DayTextMuted = Color(0xFF94A3B8)       // Light slate

// --- SLATE MODE (Deep Dark Palette) ---
val SlateCanvas = Color(0xFF0B1120)        // Deep midnight navy
val SlateSurface = Color(0xFF1E293B)       // Rich slate navy cards
val SlateSurfaceVariant = Color(0xFF26354A) // Lighter slate for depth
val SlateBorder = Color(0xFF334155)        // Slate gray border
val SlateTextPrimary = Color(0xFFF8FAFC)   // Crisp white text
val SlateTextSecondary = Color(0xFF94A3B8) // Muted slate text
val SlateTextMuted = Color(0xFF64748B)     // Subdued slate text

// Legacy Semantic Tokens (Will be derived from active theme in Theme.kt)
// These now serve as default values or dark mode defaults for backward compatibility
val EduCanvas = SlateCanvas
val EduSurface = SlateSurface
val EduSurfaceVariant = SlateSurfaceVariant
val EduBorder = SlateBorder
val EduTextPrimary = SlateTextPrimary
val EduTextSecondary = SlateTextSecondary
val EduTextMuted = SlateTextMuted

val TextPrimary = Color.Unspecified // Replaced by MaterialTheme.colorScheme.onBackground
val TextSecondary = Color.Unspecified
val TextMuted = Color.Unspecified

// Brand Overrides
val AmberPrimary = EduRoyalBlue
val AmberLight = EduRoyalBlue
val AmberDark = EduRoyalBlueDark

val EmeraldPrimary = EduTealDark
val EmeraldLight = EduTealGreen
val EmeraldDistinct = EduTealGreen

val FlameOrange = EduCoralRose
val FlameLight = EduCoralRose

val RoyalIndigo = EduRoyalBlue
val RoyalIndigoContainer = EduRoyalBlueLight

val SunnyAmber = EduSunnyAmber
val SunnyAmberContainer = EduSunnyAmberContainer

val CoralSalmon = EduCoralRose
val SkyCyan = EduSkyCyan

val ShuleeBlue = EduRoyalBlue
val ShuleeBlueLight = EduRoyalBlueLight

val FlameLightColor = EduCoralRose
val FlameDarkColor = EduCoralDark

// --- ADDED MISSING TOKENS ---
val SlateSurfaceLight = Color(0xFF2D3748) // Lighter slate for nested cards
val DaySurfaceLight = Color(0xFFEDF2F7)   // Lighter gray for day mode nested cards

val GoldBadge = Color(0xFFFFD700)
val SilverBadge = Color(0xFFC0C0C0)
val BronzeBadge = Color(0xFFCD7F32)
