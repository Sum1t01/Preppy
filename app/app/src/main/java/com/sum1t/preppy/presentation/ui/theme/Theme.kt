package com.sum1t.preppy.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class ThemePaletteSet(
    val light: ColorScheme,
    val dark: ColorScheme
)



enum class ThemePalette {
    DEFAULT_GREEN,
    OCEAN_BLUE,
    SUNSET_ORANGE,
    MONOCHROME,
    FOREST,
    NEON_PURPLE,
    COFFEE,
    MIDNIGHT
}

object ThemeRegistry {

    fun get(palette: ThemePalette): ThemePaletteSet {
        return when (palette) {

            ThemePalette.DEFAULT_GREEN ->
                ThemePaletteSet(GreenPalette.Light, GreenPalette.Dark)

            ThemePalette.OCEAN_BLUE ->
                ThemePaletteSet(OceanPalette.Light, OceanPalette.Dark)

            ThemePalette.SUNSET_ORANGE ->
                ThemePaletteSet(SunsetPalette.Light, SunsetPalette.Dark)

            ThemePalette.MONOCHROME ->
                ThemePaletteSet(MonoPalette.Light, MonoPalette.Dark)

            ThemePalette.FOREST ->
                ThemePaletteSet(ForestPalette.Light, ForestPalette.Dark)

            ThemePalette.NEON_PURPLE ->
                ThemePaletteSet(NeonPurplePalette.Light, NeonPurplePalette.Dark)

            ThemePalette.COFFEE ->
                ThemePaletteSet(CoffeePalette.Light, CoffeePalette.Dark)

            ThemePalette.MIDNIGHT ->
                ThemePaletteSet(MidnightPalette.Light, MidnightPalette.Dark)
        }
    }
}


@Composable
fun PreppyTheme(
    palette: ThemePalette,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorSet = ThemeRegistry.get(palette)

    val colorScheme = if (darkTheme) {
        colorSet.dark
    } else {
        colorSet.light
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}