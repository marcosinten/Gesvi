package com.gestionviajes.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Paleta de marca. Los valores crudos solo viven dentro del Design System.
private val Indigo = Color(0xFF322FA8)
private val IndigoDark = Color(0xFF191766)
private val IndigoSoft = Color(0xFFE3E4FF)
private val Mist = Color(0xFFF1F5FF)
private val Ink = Color(0xFF20243D)
private val MutedInk = Color(0xFF5B6178)
private val Sky = Color(0xFF4DB7E8)
private val SkyDark = Color(0xFF17698E)
private val SkySoft = Color(0xFFCDEEFF)
private val Green = Color(0xFF55BE7C)
private val GreenDark = Color(0xFF247849)
private val GreenSoft = Color(0xFFD8F5E2)
private val Yellow = Color(0xFFF3BC4C)
private val YellowDark = Color(0xFF755500)
private val YellowSoft = Color(0xFFFFEDBE)
private val Pink = Color(0xFFED7CA2)
private val PinkDark = Color(0xFF963B61)
private val PinkSoft = Color(0xFFFFD9E5)

/** Colores de asiento organizados por tema. */
object SeatColorTokens {
    object Light {
        val empty = Color.White
        val reserved = Color(0xFFFFCDD8)
        val partial = YellowSoft
        val paid = GreenSoft
        val onSeat = Ink
    }

    object Dark {
        val empty = Color(0xFF2A304B)
        val reserved = Color(0xFF702B46)
        val partial = Color(0xFF5B460D)
        val paid = Color(0xFF174C31)
        val onSeat = Color(0xFFF3F4FF)
    }
}

/** Tokens semánticos y acentos que no forman parte del esquema Material 3. */
@Immutable
data class AppExtendedColors(
    val brandSurface: Color,
    val onBrandSurface: Color,
    val seatEmpty: Color,
    val seatReserved: Color,
    val seatPartial: Color,
    val seatPaid: Color,
    val onSeat: Color,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val accentGreen: Color,
    val accentGreenContainer: Color,
    val accentYellow: Color,
    val accentYellowContainer: Color,
    val accentSky: Color,
    val accentSkyContainer: Color,
    val accentPink: Color,
    val accentPinkContainer: Color,
    val incomePositive: Color,
    val balancePending: Color,
)

val LightExtendedColors = AppExtendedColors(
    brandSurface = Indigo,
    onBrandSurface = Color.White,
    seatEmpty = SeatColorTokens.Light.empty,
    seatReserved = SeatColorTokens.Light.reserved,
    seatPartial = SeatColorTokens.Light.partial,
    seatPaid = SeatColorTokens.Light.paid,
    onSeat = SeatColorTokens.Light.onSeat,
    success = GreenDark,
    onSuccess = Color.White,
    successContainer = GreenSoft,
    onSuccessContainer = Color(0xFF0D4A2A),
    warning = YellowDark,
    onWarning = Color.White,
    warningContainer = YellowSoft,
    onWarningContainer = Color(0xFF4A3600),
    accentGreen = Green,
    accentGreenContainer = GreenSoft,
    accentYellow = Yellow,
    accentYellowContainer = YellowSoft,
    accentSky = Sky,
    accentSkyContainer = SkySoft,
    accentPink = Pink,
    accentPinkContainer = PinkSoft,
    incomePositive = GreenDark,
    balancePending = Color(0xFF9A5900),
)

val DarkExtendedColors = AppExtendedColors(
    brandSurface = Color(0xFF4541C4),
    onBrandSurface = Color.White,
    seatEmpty = SeatColorTokens.Dark.empty,
    seatReserved = SeatColorTokens.Dark.reserved,
    seatPartial = SeatColorTokens.Dark.partial,
    seatPaid = SeatColorTokens.Dark.paid,
    onSeat = SeatColorTokens.Dark.onSeat,
    success = Color(0xFF87D9A5),
    onSuccess = Color(0xFF00391D),
    successContainer = Color(0xFF174C31),
    onSuccessContainer = Color(0xFFB6F2CA),
    warning = Color(0xFFFFD66D),
    onWarning = Color(0xFF3E2E00),
    warningContainer = Color(0xFF5B460D),
    onWarningContainer = Color(0xFFFFE7A5),
    accentGreen = Color(0xFF78D99A),
    accentGreenContainer = Color(0xFF174C31),
    accentYellow = Color(0xFFFFD66D),
    accentYellowContainer = Color(0xFF5B460D),
    accentSky = Color(0xFF83D2F5),
    accentSkyContainer = Color(0xFF154A62),
    accentPink = Color(0xFFFFA9C4),
    accentPinkContainer = Color(0xFF672440),
    incomePositive = Color(0xFF87D9A5),
    balancePending = Color(0xFFFFCA75),
)

val LocalAppExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val AppLightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoSoft,
    onPrimaryContainer = IndigoDark,
    secondary = SkyDark,
    onSecondary = Color.White,
    secondaryContainer = SkySoft,
    onSecondaryContainer = Color(0xFF063B52),
    tertiary = PinkDark,
    onTertiary = Color.White,
    tertiaryContainer = PinkSoft,
    onTertiaryContainer = Color(0xFF5B1936),
    background = Mist,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE3EAF7),
    onSurfaceVariant = MutedInk,
    outline = Color(0xFF777E96),
    outlineVariant = Color(0xFFC8D0E2),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

val AppDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC1C2FF),
    onPrimary = IndigoDark,
    primaryContainer = Color(0xFF353294),
    onPrimaryContainer = Color(0xFFE2E1FF),
    secondary = Color(0xFF8DD7F8),
    onSecondary = Color(0xFF003548),
    secondaryContainer = Color(0xFF154A62),
    onSecondaryContainer = Color(0xFFCDEEFF),
    tertiary = Color(0xFFFFB1CB),
    onTertiary = Color(0xFF5D1137),
    tertiaryContainer = Color(0xFF7A294E),
    onTertiaryContainer = Color(0xFFFFD9E5),
    background = Color(0xFF101426),
    onBackground = Color(0xFFF3F4FF),
    surface = Color(0xFF191D34),
    onSurface = Color(0xFFF3F4FF),
    surfaceVariant = Color(0xFF2A304B),
    onSurfaceVariant = Color(0xFFC4C9DD),
    outline = Color(0xFF9299B3),
    outlineVariant = Color(0xFF444A64),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)
