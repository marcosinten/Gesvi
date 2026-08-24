package com.gestionviajes.feature.passenger_list.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.People
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppPlaceholderScreen

@Composable
fun PassengerListScreen(
    onNavigateBack: () -> Unit,
) {
    AppPlaceholderScreen(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.passenger_list_title),
        message = stringResource(R.string.passenger_list_placeholder),
        icon = Icons.Rounded.People,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
    )
}
