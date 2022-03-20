package com.mospolytech.features.base.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

//private val AppDarkColorScheme = darkColorScheme(
//    primary = Blue80,
//    onPrimary = Blue20,
//    primaryContainer = Blue30,
//    onPrimaryContainer = Blue90,
//    inversePrimary = Blue40,
//    secondary = DarkBlue80,
//    onSecondary = DarkBlue20,
//    secondaryContainer = DarkBlue30,
//    onSecondaryContainer = DarkBlue90,
//    tertiary = Yellow80,
//    onTertiary = Yellow20,
//    tertiaryContainer = Yellow30,
//    onTertiaryContainer = Yellow90,
//    error = Red80,
//    onError = Red20,
//    errorContainer = Red30,
//    onErrorContainer = Red90,
//    background = Grey10,
//    onBackground = Grey90,
//    surface = Grey10,
//    onSurface = Grey80,
//    inverseSurface = Grey90,
//    inverseOnSurface = Grey20,
//    surfaceVariant = BlueGrey30,
//    onSurfaceVariant = BlueGrey80,
//    outline = BlueGrey60
//)
//
//private val AppLightColorScheme = lightColorScheme(
//    primary = Blue40,
//    onPrimary = Color.White,
//    primaryContainer = Blue90,
//    onPrimaryContainer = Blue10,
//    inversePrimary = Blue80,
//    secondary = DarkBlue40,
//    onSecondary = Color.White,
//    secondaryContainer = DarkBlue90,
//    onSecondaryContainer = DarkBlue10,
//    tertiary = Yellow40,
//    onTertiary = Color.White,
//    tertiaryContainer = Yellow90,
//    onTertiaryContainer = Yellow10,
//    error = Red40,
//    onError = Color.White,
//    errorContainer = Red90,
//    onErrorContainer = Red10,
//    background = Grey99,
//    onBackground = Grey10,
//    surface = Grey99,
//    onSurface = Grey10,
//    inverseSurface = Grey20,
//    inverseOnSurface = Grey95,
//    surfaceVariant = BlueGrey90,
//    onSurfaceVariant = BlueGrey30,
//    outline = BlueGrey50
//)


private val AppLightColorScheme = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
)
private val AppDarkColorScheme = darkColorScheme(

    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
)


private val AppDarkColorScheme2 = darkColors(
    primary = AppDarkColorScheme.primary,
    onPrimary = AppDarkColorScheme.onPrimary,
    secondary = AppDarkColorScheme.secondary,
    onSecondary = AppDarkColorScheme.onSecondary,
    error = AppDarkColorScheme.error,
    onError = AppDarkColorScheme.onError,
    background = AppDarkColorScheme.background,
    onBackground = AppDarkColorScheme.onBackground,
    surface = AppDarkColorScheme.surface,
    onSurface = AppDarkColorScheme.onSurface,

    primaryVariant = AppDarkColorScheme.primary,
    secondaryVariant = AppDarkColorScheme.secondary,
)

private val AppLightColorScheme2 = lightColors(
    primary = AppLightColorScheme.primary,
    onPrimary = AppLightColorScheme.onPrimary,
    secondary = AppLightColorScheme.secondary,
    onSecondary = AppLightColorScheme.onSecondary,
    error = AppLightColorScheme.error,
    onError = AppLightColorScheme.onError,
    background = AppLightColorScheme.background,
    onBackground = AppLightColorScheme.onBackground,
    surface = AppLightColorScheme.surface,
    onSurface = AppLightColorScheme.onSurface,

    primaryVariant = AppLightColorScheme.primary,
    secondaryVariant = AppLightColorScheme.secondary,
)

@Composable
fun MospolyhelperTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    //val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val myColorScheme = when {
        isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        isDarkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }

    val myColorScheme2 = when {
        isDarkTheme -> AppDarkColorScheme2
        else -> AppLightColorScheme2
    }



    androidx.compose.material.MaterialTheme(
        colors = myColorScheme2,
        typography = AppTypography2
    ) {
        MaterialTheme(
            colorScheme = myColorScheme,
            typography = AppTypography
        ) {
            content()
        }
    }
}