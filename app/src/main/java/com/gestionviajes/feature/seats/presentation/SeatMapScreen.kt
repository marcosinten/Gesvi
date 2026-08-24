package com.gestionviajes.feature.seats.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppPlaceholderScreen

@Suppress("UNUSED_PARAMETER")
@Composable
fun SeatMapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPassengerList: (Long) -> Unit,
) {
    AppPlaceholderScreen(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.seat_map_title),
        message = stringResource(R.string.seat_map_placeholder),
        icon = Icons.Rounded.EventSeat,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
    )
}
