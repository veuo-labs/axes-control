package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = BgDark,
    primaryContainer = AccentGoldContainer,
    onPrimaryContainer = AccentGold,
    secondary = AccentGold,
    onSecondary = BgDark,
    background = BgDark,
    onBackground = TextLight,
    surface = SurfaceDark,
    onSurface = TextLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextMuted,
    outline = OutlineSubtle,
    outlineVariant = OutlineDivider,
    error = GuardDanger,
    onError = BgDark
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
