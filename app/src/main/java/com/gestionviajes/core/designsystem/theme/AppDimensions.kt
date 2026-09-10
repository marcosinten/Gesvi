package com.gestionviajes.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Dimensiones semánticas que no representan separación entre elementos. */
@Immutable
data class AppDimensions(
    val minimumTouchTarget: Dp = 48.dp,
    val buttonHeight: Dp = 56.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val illustrationContainer: Dp = 72.dp,
    val headerIconContainer: Dp = 64.dp,
    val bottomBarHeight: Dp = 80.dp,
    val bottomBarIconContainer: Dp = 40.dp,
    val pagerIndicator: Dp = 40.dp,
    val pagerDot: Dp = 10.dp,
    val seatCell: Dp = 72.dp,
    val cardElevation: Dp = 5.dp,
    val buttonElevation: Dp = 3.dp,
    val floatingElevation: Dp = 12.dp,
)

val LocalAppDimensions = staticCompositionLocalOf { AppDimensions() }
