package com.gestionviajes.feature.trips.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppPlaceholderScreen

@Composable
fun EditTourPlaceholderScreen(onNavigateBack: () -> Unit) {
    TourPlaceholderScreen(
        titleRes = R.string.edit_tour_title,
        messageRes = R.string.edit_tour_placeholder,
        icon = Icons.Rounded.Edit,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun TourPlaceholderScreen(
    @StringRes titleRes: Int,
    @StringRes messageRes: Int,
    icon: ImageVector,
    onNavigateBack: () -> Unit,
) {
    AppPlaceholderScreen(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(titleRes),
        message = stringResource(messageRes),
        icon = icon,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
    )
}
