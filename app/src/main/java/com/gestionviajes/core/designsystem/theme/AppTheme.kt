package com.gestionviajes.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

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

    CompositionLocalProvider(
        LocalAppExtendedColors provides extendedColors,
        LocalAppSpacing provides AppSpacing(),
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
}
