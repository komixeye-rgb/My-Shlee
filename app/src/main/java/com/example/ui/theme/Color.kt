package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- EMERALD SCHOLAR UNEB DESIGN SYSTEM (STITCH) ---

// Brand Colors
val EmeraldPrimary = Color(0xFF006C49)       // Deep emerald for mastery & success
val EmeraldContainer = Color(0xFF10B981)     // Vibrant emerald container
val RoyalIndigo = Color(0xFF4F46E5)          // Academic rigor & focus sessions
val SunnyAmber = Color(0xFFF59E0B)           // Gamification streaks & badges
val CoralSalmon = Color(0xFFF43F5E)          // Alerts & physics/chemistry
val SkyCyan = Color(0xFF0EA5E9)              // Downloads & mock tests

// Shared Brand Semantic Tokens
val EduRoyalBlue = RoyalIndigo
val EduRoyalBlueDark = Color(0xFF3730A3)
val EduRoyalBlueLight = Color(0xFF6366F1)
val EduTealGreen = EmeraldContainer
val EduTealDark = EmeraldPrimary
val EduTealLight = Color(0xFF34D399)
val EduCoralRose = CoralSalmon
val EduCoralDark = Color(0xFFBA1A1A)
val EduSunnyAmber = SunnyAmber
val EduSunnyAmberContainer = Color(0xFFD97706)
val EduSkyCyan = SkyCyan

// --- DAY MODE (High Contrast Light Palette) ---
val DayCanvas = Color(0xFFF8FAFC)          // Crisp ultra-light slate
val DaySurface = Color(0xFFFFFFFF)         // Pure white surface cards
val DaySurfaceVariant = Color(0xFFD8E3FB)  // Tinted surface variant
val DayBorder = Color(0xFFE2E8F0)          // Subtle slate border
val DayTextPrimary = Color(0xFF111C2D)     // Deep navy for sharp text (Slate)
val DayTextSecondary = Color(0xFF3C4A42)   // Muted green-gray
val DayTextMuted = Color(0xFF6C7A71)       // Subdued outline text

// --- SLATE MODE (Deep Dark Palette) ---
// Dark mode is maintained as the "Slate" variant from previous iterations, 
// but updated to feel more cohesive with the Emerald brand.
val SlateCanvas = Color(0xFF0B1120)        // Deep midnight navy
val SlateSurface = Color(0xFF1E293B)       // Rich slate navy cards
val SlateSurfaceVariant = Color(0xFF26354A) // Lighter slate for depth
val SlateBorder = Color(0xFF334155)        // Slate gray border
val SlateTextPrimary = Color(0xFFF8FAFC)   // Crisp white text
val SlateTextSecondary = Color(0xFF94A3B8) // Muted slate text
val SlateTextMuted = Color(0xFF64748B)     // Subdued slate text

// Legacy Semantic Tokens (Derive from active theme in Theme.kt)
val EduCanvas = SlateCanvas
val EduSurface = SlateSurface
val EduSurfaceVariant = SlateSurfaceVariant
val EduBorder = SlateBorder
val EduTextPrimary = SlateTextPrimary
val EduTextSecondary = SlateTextSecondary
val EduTextMuted = SlateTextMuted

val AmberPrimary = SunnyAmber
val AmberLight = SunnyAmber
val AmberDark = Color(0xFFD97706)

val EmeraldLight = EmeraldContainer
val EmeraldDistinct = EmeraldContainer

val FlameOrange = CoralSalmon
val FlameLight = CoralSalmon
val FlameLightColor = CoralSalmon
val FlameDarkColor = Color(0xFFBA1A1A)

val ShuleeBlue = RoyalIndigo
val ShuleeBlueLight = EduRoyalBlueLight

val GoldBadge = Color(0xFFFFD700)
val SilverBadge = Color(0xFFC0C0C0)
val BronzeBadge = Color(0xFFCD7F32)

val SlateSurfaceLight = Color(0xFF2D3748)
val DaySurfaceLight = Color(0xFFEDF2F7)
