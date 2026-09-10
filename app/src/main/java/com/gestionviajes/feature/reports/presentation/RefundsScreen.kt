package com.gestionviajes.feature.reports.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme

/**
 * Módulo de devoluciones. Sin reglas de negocio implementadas todavía: Gesvi
 * aún no registra devoluciones, por lo que se muestra un estado vacío. No se
 * inventan flujos ni fuentes de datos nuevos hasta requerimiento explícito.
 */
@Composable
fun RefundsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit = {},
) {
    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(ReportsLocale),
        title = stringResource(R.string.refunds_title),
        subtitle = stringResource(R.string.refunds_subtitle),
        headerIcon = Icons.Rounded.Assessment,
        onNavigateBack = onNavigateBack,
        bottomBar = {
            ReportsBottomNavigation(
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(AppTheme.spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            AppEmptyState(
                title = stringResource(R.string.refunds_empty_title),
                message = stringResource(R.string.refunds_empty_message),
                icon = Icons.Rounded.Payments,
            )
        }
    }
}