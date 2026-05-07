package com.aecr.gamex.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════
//  AECR GAMEX — Brand Color System
// ═══════════════════════════════════════════

object GameXColors {
    // Neon Accents
    val NeonCyan     = Color(0xFF00F5FF)
    val NeonGreen    = Color(0xFF39FF14)
    val NeonBlue     = Color(0xFF0080FF)
    val NeonPurple   = Color(0xFFBF00FF)
    val NeonOrange   = Color(0xFFFF6600)

    // Background Layers
    val BgPrimary    = Color(0xFF080C14)
    val BgSecondary  = Color(0xFF0D1421)
    val BgCard       = Color(0xFF111827)
    val BgCardElev   = Color(0xFF162032)
    val BgSurface    = Color(0xFF1A2540)

    // Text
    val TextPrimary  = Color(0xFFFFFFFF)
    val TextSecond   = Color(0xFF8899BB)
    val TextAccent   = Color(0xFF00F5FF)
    val TextMuted    = Color(0xFF445566)

    // Status
    val StatusEx     = Color(0xFF39FF14)
    val StatusGood   = Color(0xFF00F5FF)
    val StatusWarn   = Color(0xFFFF8C00)
    val StatusDanger = Color(0xFFFF2244)

    // Glow (with alpha)
    val GlowCyan     = Color(0x3300F5FF)
    val GlowGreen    = Color(0x3339FF14)
    val GlowBlue     = Color(0x330080FF)

    // Divider
    val Divider      = Color(0xFF1A2540)
}

// ═══════════════════════════════════════════
//  Dark Material3 Color Scheme
// ═══════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary             = GameXColors.NeonCyan,
    onPrimary           = GameXColors.BgPrimary,
    primaryContainer    = GameXColors.BgSurface,
    onPrimaryContainer  = GameXColors.NeonCyan,
    secondary           = GameXColors.NeonGreen,
    onSecondary         = GameXColors.BgPrimary,
    secondaryContainer  = Color(0xFF0D2010),
    onSecondaryContainer= GameXColors.NeonGreen,
    tertiary            = GameXColors.NeonBlue,
    onTertiary          = GameXColors.BgPrimary,
    background          = GameXColors.BgPrimary,
    onBackground        = GameXColors.TextPrimary,
    surface             = GameXColors.BgCard,
    onSurface           = GameXColors.TextPrimary,
    surfaceVariant      = GameXColors.BgSurface,
    onSurfaceVariant    = GameXColors.TextSecond,
    outline             = GameXColors.BgSurface,
    error               = GameXColors.StatusDanger,
    onError             = Color.White,
)

// ═══════════════════════════════════════════
//  Typography — Orbitron-inspired system font
// ═══════════════════════════════════════════

val GameXTypography = Typography(
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Black,
        fontSize    = 32.sp,
        letterSpacing = 4.sp,
        color       = GameXColors.TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.ExtraBold,
        fontSize    = 24.sp,
        letterSpacing = 3.sp,
        color       = GameXColors.NeonCyan
    ),
    headlineLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 20.sp,
        letterSpacing = 2.sp,
        color       = GameXColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 16.sp,
        letterSpacing = 1.5.sp,
        color       = GameXColors.TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 18.sp,
        letterSpacing = 1.sp
    ),
    titleMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 14.sp,
        letterSpacing = 1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 12.sp,
        letterSpacing = 1.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Medium,
        fontSize    = 10.sp,
        letterSpacing = 1.sp
    )
)

// ═══════════════════════════════════════════
//  Composable Theme Entry Point
// ═══════════════════════════════════════════

@Composable
fun AECRGameXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = GameXTypography,
        content     = content
    )
}
