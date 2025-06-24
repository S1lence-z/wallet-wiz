package com.example.walletwiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.walletwiz.ui.theme.AppTypography

private val DarkColorPalette = darkColorScheme(
    primary = Primary,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface,
    error = Error
)

@Composable
fun WalletWizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = DarkColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = Shapes,
        content = content
    )
}

