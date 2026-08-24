package com.gestionviajes.feature.bookings.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppPlaceholderScreen

@Suppress("UNUSED_PARAMETER")
@Composable
fun BookingListScreen(
    tripId: Long,
    onNavigateBack: () -> Unit,
) {
    AppPlaceholderScreen(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.bookings_title),
        message = stringResource(R.string.bookings_placeholder),
        icon = Icons.Rounded.People,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
    )
}
