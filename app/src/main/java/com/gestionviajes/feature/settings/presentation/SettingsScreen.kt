package com.gestionviajes.feature.settings.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppPlaceholderScreen

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
) {
    AppPlaceholderScreen(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.settings_title),
        message = stringResource(R.string.settings_placeholder),
        icon = Icons.Rounded.Settings,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
    )
}
