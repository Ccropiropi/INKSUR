package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SumiDarkColorScheme = darkColorScheme(
    primary = RicePaperBase,
    onPrimary = SumiBlack,
    secondary = VermilionRed,
    onSecondary = Color.White,
    tertiary = IndigoBlue,
    onTertiary = Color.White,
    background = SumiBlack,
    onBackground = RicePaperBase,
    surface = InkCharcoal,
    onSurface = RicePaperBase,
    surfaceVariant = InkMedium,
    onSurfaceVariant = RicePaperDarker
)

private val SumiLightColorScheme = lightColorScheme(
    primary = SumiBlack,
    onPrimary = RicePaperBase,
    secondary = VermilionRed,
    onSecondary = Color.White,
    tertiary = IndigoBlue,
    onTertiary = Color.White,
    background = RicePaperBase,
    onBackground = SumiBlack,
    surface = CardParchment,
    onSurface = SumiBlack,
    surfaceVariant = RicePaperDarker,
    onSurfaceVariant = InkMedium
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We use deliberate Sumi-e aesthetic rather than random wallpaper colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SumiDarkColorScheme else SumiLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
