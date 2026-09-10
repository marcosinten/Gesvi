package com.gestionviajes.feature.history.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppBottomNavigation
import com.gestionviajes.core.designsystem.component.AppBottomNavigationItem
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppHeroHeader
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.presentation.TripViewModel
import com.gestionviajes.feature.trips.presentation.screen.TourCard
import kotlinx.coroutines.delay

private const val TourDurationMillis = 48L * 60L * 60L * 1_000L
private const val HistoryClockRefreshMillis = 60_000L

private enum class TourCategory {
    Active,
    Upcoming,
    Completed,
}

@Composable
fun HistoryScreen(
    onNavigateToReports: () -> Unit,
    onNavigateToHome: () -> Unit,
    onOpenTour: (Long) -> Unit,
    viewModel: TripViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteUiState by historyViewModel.deleteUiState.collectAsStateWithLifecycle()
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val navigationItems = listOf(
        AppBottomNavigationItem(
            label = stringResource(R.string.reports_title),
            icon = Icons.Rounded.Assessment,
        ),
        AppBottomNavigationItem(
            label = stringResource(R.string.home_title),
            icon = Icons.Rounded.Home,
        ),
        AppBottomNavigationItem(
            label = stringResource(R.string.history_title),
            icon = Icons.Rounded.History,
        ),
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(HistoryClockRefreshMillis)
            nowMillis = System.currentTimeMillis()
        }
    }

    val groupedTours = remember(uiState.trips, nowMillis) {
        uiState.trips.groupBy { trip -> trip.categoryAt(nowMillis) }
    }

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.history_title),
        subtitle = stringResource(R.string.history_subtitle),
        headerIcon = Icons.Rounded.History,
        showHeader = false,
        bottomBar = {
            AppBottomNavigation(
                items = navigationItems,
                selectedIndex = 2,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onNavigateToReports()
                        1 -> onNavigateToHome()
                        else -> Unit
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = AppTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium),
        ) {
            item(key = "history-header") {
                AppHeroHeader(
                    title = stringResource(R.string.history_title),
                    icon = Icons.Rounded.History,
                    eyebrow = stringResource(R.string.app_name).uppercase(),
                    subtitle = stringResource(R.string.history_subtitle),
                )
            }

            when {
                uiState.isLoading && uiState.trips.isEmpty() -> item(key = "history-loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.spacing.extraLarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.trips.isEmpty() -> item(key = "history-empty") {
                    AppEmptyState(
                        title = stringResource(R.string.empty_tours_title),
                        message = if (uiState.errorMessage != null) {
                            stringResource(R.string.tours_load_error)
                        } else {
                            stringResource(R.string.empty_tours_message)
                        },
                        icon = Icons.Rounded.DirectionsBus,
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.large),
                    )
                }

                else -> {
                    historySection(
                        title = R.string.history_active_section,
                        tours = groupedTours[TourCategory.Active].orEmpty()
                            .sortedBy(Trip::dateMillis),
                        onOpenTour = onOpenTour,
                        onDeleteTour = { tourId ->
                            historyViewModel.onEvent(HistoryDeleteEvent.DeleteRequested(tourId))
                        },
                    )
                    historySection(
                        title = R.string.history_upcoming_section,
                        tours = groupedTours[TourCategory.Upcoming].orEmpty()
                            .sortedBy(Trip::dateMillis),
                        onOpenTour = onOpenTour,
                        onDeleteTour = { tourId ->
                            historyViewModel.onEvent(HistoryDeleteEvent.DeleteRequested(tourId))
                        },
                    )
                    historySection(
                        title = R.string.history_completed_section,
                        tours = groupedTours[TourCategory.Completed].orEmpty()
                            .sortedByDescending(Trip::dateMillis),
                        onOpenTour = onOpenTour,
                        onDeleteTour = { tourId ->
                            historyViewModel.onEvent(HistoryDeleteEvent.DeleteRequested(tourId))
                        },
                    )
                }
            }
        }
    }
    if (deleteUiState.tourIdPendingDeletion != null) {
        AlertDialog(
            onDismissRequest = {
                historyViewModel.onEvent(HistoryDeleteEvent.DismissDelete)
            },
            title = { Text(stringResource(R.string.delete_tour_title)) },
            text = {
                Text(stringResource(R.string.delete_tour_message))
            },
            confirmButton = {
                TextButton(
                    onClick = { historyViewModel.onEvent(HistoryDeleteEvent.ConfirmDelete) },
                    enabled = !deleteUiState.isDeleting,
                ) {
                    Text(stringResource(R.string.delete_action))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { historyViewModel.onEvent(HistoryDeleteEvent.DismissDelete) },
                    enabled = !deleteUiState.isDeleting,
                ) {
                    Text(stringResource(R.string.cancel_action))
                }
            },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.historySection(
    title: Int,
    tours: List<Trip>,
    onOpenTour: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
) {
    item(key = "section-$title") {
        Text(
            text = stringResource(title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.large),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
    if (tours.isEmpty()) {
        item(key = "empty-$title") {
            Text(
                text = stringResource(R.string.history_empty_section),
                modifier = Modifier.padding(horizontal = AppTheme.spacing.large),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    } else {
        items(
            items = tours,
            key = Trip::id,
        ) { trip ->
            TourCard(
                trip = trip,
                onOpen = { onOpenTour(trip.id) },
                secondaryActionText = stringResource(R.string.delete_tour_action),
                onSecondaryAction = { onDeleteTour(trip.id) },
                secondaryActionFullWidth = true,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.large),
            )
        }
    }
}

private fun Trip.categoryAt(nowMillis: Long): TourCategory = when {
    nowMillis < dateMillis -> TourCategory.Upcoming
    nowMillis - dateMillis < TourDurationMillis -> TourCategory.Active
    else -> TourCategory.Completed
}
