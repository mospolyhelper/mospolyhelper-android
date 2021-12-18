package com.mospolytech.features.base.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AppDarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,
    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = BlueGrey60
)

private val AppLightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,
    secondary = DarkBlue40,
    onSecondary = Color.White,
    secondaryContainer = DarkBlue90,
    onSecondaryContainer = DarkBlue10,
    tertiary = Yellow40,
    onTertiary = Color.White,
    tertiaryContainer = Yellow90,
    onTertiaryContainer = Yellow10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = BlueGrey90,
    onSurfaceVariant = BlueGrey30,
    outline = BlueGrey50
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