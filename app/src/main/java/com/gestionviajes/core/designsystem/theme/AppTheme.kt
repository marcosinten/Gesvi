package com.gestionviajes.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Tema principal de la aplicación. Envuelve todas las pantallas.
 *
 * Provee colores (M3 + extendidos), tipografía, formas y espaciados.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalAppExtendedColors provides extendedColors,
        LocalAppSpacing provides AppSpacing(),
        LocalAppDimensions provides AppDimensions(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

/**
 * Objeto auxiliar para acceder fácilmente a elementos del Design System
 * dentro de un contexto `@Composable`.
 *
 * Ejemplo: `AppTheme.spacing.medium`, `AppTheme.extendedColors.seatPaid`
 */
object AppTheme {
    val extendedColors: AppExtendedColors
        @Composable
        get() = LocalAppExtendedColors.current

    val spacing: AppSpacing
        @Composable
        get() = LocalAppSpacing.current

    val dimensions: AppDimensions
        @Composable
        get() = LocalAppDimensions.current
}
