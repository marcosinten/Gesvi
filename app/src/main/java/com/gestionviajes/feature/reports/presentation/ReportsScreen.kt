package com.gestionviajes.feature.reports.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppIconBadge
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme

@Composable
fun ReportsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenTripReport: () -> Unit,
    onOpenTourReport: () -> Unit,
    onOpenRefunds: () -> Unit,
) {
    ReportsMenuContent(
        onNavigateToHome = onNavigateToHome,
        onNavigateToHistory = onNavigateToHistory,
        onOpenTripReport = onOpenTripReport,
        onOpenTourReport = onOpenTourReport,
        onOpenRefunds = onOpenRefunds,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun ReportsMenuContent(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenTripReport: () -> Unit,
    onOpenTourReport: () -> Unit,
    onOpenRefunds: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(ReportsLocale),
        title = stringResource(R.string.reports_title),
        subtitle = stringResource(R.string.reports_menu_subtitle),
        headerIcon = Icons.Rounded.Assessment,
        bottomBar = {
            ReportsBottomNavigation(
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(innerPadding),
            contentPadding = PaddingValues(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            item(key = "report-menu-trip") {
                ReportMenuCard(
                    title = stringResource(R.string.trip_report_title),
                    description = stringResource(R.string.trip_report_subtitle),
                    icon = Icons.Rounded.DirectionsBus,
                    containerColor = colors.accentSkyContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = onOpenTripReport,
                )
            }
            item(key = "report-menu-tour") {
                ReportMenuCard(
                    title = stringResource(R.string.tour_report_title),
                    description = stringResource(R.string.tour_report_subtitle),
                    icon = Icons.Rounded.History,
                    containerColor = colors.accentGreenContainer,
                    contentColor = colors.onSuccessContainer,
                    onClick = onOpenTourReport,
                )
            }
            item(key = "report-menu-refunds") {
                ReportMenuCard(
                    title = stringResource(R.string.refunds_title),
                    description = stringResource(R.string.refunds_subtitle),
                    icon = Icons.Rounded.Payments,
                    containerColor = colors.accentPinkContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = onOpenRefunds,
                )
            }
        }
    }
}

@Composable
private fun ReportMenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    val spacing = AppTheme.spacing

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.large),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIconBadge(
                imageVector = icon,
                contentDescription = null,
                containerColor = containerColor,
                contentColor = contentColor,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            ) {
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}