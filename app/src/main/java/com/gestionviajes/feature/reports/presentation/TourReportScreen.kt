package com.gestionviajes.feature.reports.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppScreenScaffold

/**
 * Informe de tour: historial registrado completo del tour. Lista todos los
 * bookings del Tour sin filtrarlos por tramo. Deriva del mismo estado de
 * dominio que el mapa y el resto de pantallas.
 */
@Composable
fun TourReportScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit = {},
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = stringResource(R.string.tour_report_title)
    val subtitle = stringResource(R.string.tour_report_subtitle)

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(ReportsLocale),
        title = title,
        subtitle = subtitle,
        headerIcon = Icons.Rounded.Assessment,
        onNavigateBack = onNavigateBack,
        bottomBar = {
            ReportsBottomNavigation(
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
            )
        },
    ) { innerPadding ->
        ReportContent(
            uiState = uiState,
            onTourSelected = viewModel::selectTour,
            title = title,
            subtitle = subtitle,
            bookingFilter = { detail -> detail.bookings },
            showHeroHeader = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}