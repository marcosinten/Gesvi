package com.gestionviajes.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Paleta de marca ─────────────────────────────────────────────────────────
// Colores base del sistema — no usar directamente en Composables.
// Acceder siempre a través de AppTheme.extendedColors o MaterialTheme.colorScheme.

private val Blue80   = Color(0xFF9DCEFF)
private val Blue40   = Color(0xFF0061A4)
private val Orange80 = Color(0xFFFFB86B)
private val Orange40 = Color(0xFF8C4900)

// ─── Colores de asientos ─────────────────────────────────────────────────────

/** Colores de asiento organizados por tema. */
object SeatColorTokens {
    object Light {
        val empty    = Color(0xFFE8EDF2)   // gris muy claro
        val reserved = Color(0xFFFFF3B2)   // amarillo suave
        val partial  = Color(0xFFFFCC80)   // naranja suave
        val paid     = Color(0xFFA5D6A7)   // verde suave
        val selected = Color(0xFF90CAF9)   // azul claro
        val disabled = Color(0xFFCFD8DC)   // gris apagado
        val onSeat   = Color(0xFF1C1B1F)   // texto sobre celda
    }
    object Dark {
        val empty    = Color(0xFF2A3240)
        val reserved = Color(0xFF5C4A00)
        val partial  = Color(0xFF7A4800)
        val paid     = Color(0xFF1B5E20)
        val selected = Color(0xFF0D47A1)
        val disabled = Color(0xFF37474F)
        val onSeat   = Color(0xFFE6E1E5)
    }
}

// ─── Colores extendidos (fuera del esquema M3 estándar) ──────────────────────

/**
 * Colores adicionales que Material 3 no cubre: estados de asientos,
 * colores de economía positiva/pendiente, etc.
 *
 * Acceso en Composables: `AppTheme.extendedColors.seatPaid`
 */
@Immutable
data class AppExtendedColors(
    // Asientos
    val seatEmpty: Color,
    val seatReserved: Color,
    val seatPartial: Color,
    val seatPaid: Color,
    val seatSelected: Color,
    val seatDisabled: Color,
    val onSeat: Color,
    // Economía
    val incomePositive: Color,
    val balancePending: Color,
)

val LightExtendedColors = AppExtendedColors(
    seatEmpty    = SeatColorTokens.Light.empty,
    seatReserved = SeatColorTokens.Light.reserved,
    seatPartial  = SeatColorTokens.Light.partial,
    seatPaid     = SeatColorTokens.Light.paid,
    seatSelected = SeatColorTokens.Light.selected,
    seatDisabled = SeatColorTokens.Light.disabled,
    onSeat       = SeatColorTokens.Light.onSeat,
    incomePositive = Color(0xFF2E7D32),
    balancePending = Color(0xFFE65100),
)

val DarkExtendedColors = AppExtendedColors(
    seatEmpty    = SeatColorTokens.Dark.empty,
    seatReserved = SeatColorTokens.Dark.reserved,
    seatPartial  = SeatColorTokens.Dark.partial,
    seatPaid     = SeatColorTokens.Dark.paid,
    seatSelected = SeatColorTokens.Dark.selected,
    seatDisabled = SeatColorTokens.Dark.disabled,
    onSeat       = SeatColorTokens.Dark.onSeat,
    incomePositive = Color(0xFF81C784),
    balancePending = Color(0xFFFFB74D),
)

// ─── CompositionLocal ─────────────────────────────────────────────────────────

val LocalAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }

// ─── Esquemas de color M3 ─────────────────────────────────────────────────────

val AppLightColorScheme = lightColorScheme(
    primary            = Blue40,
    onPrimary          = Color.White,
    secondary          = Orange40,
    onSecondary        = Color.White,
    surface            = Color(0xFFFEFBFF),
    onSurface          = Color(0xFF1C1B1F),
    background         = Color(0xFFFEFBFF),
    onBackground       = Color(0xFF1C1B1F),
    surfaceVariant     = Color(0xFFDDE3EA),
    onSurfaceVariant   = Color(0xFF41484D),
)

val AppDarkColorScheme = darkColorScheme(
    primary            = Blue80,
    onPrimary          = Color(0xFF003258),
    secondary          = Orange80,
    onSecondary        = Color(0xFF4A2800),
    surface            = Color(0xFF1C1B1F),
    onSurface          = Color(0xFFE6E1E5),
    background         = Color(0xFF1C1B1F),
    onBackground       = Color(0xFFE6E1E5),
    surfaceVariant     = Color(0xFF41484D),
    onSurfaceVariant   = Color(0xFFC1C7CE),
)
