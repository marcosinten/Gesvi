package com.gestionviajes.feature.trips.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppBottomNavigation
import com.gestionviajes.core.designsystem.component.AppBottomNavigationItem
import com.gestionviajes.core.designsystem.component.AppButton
import com.gestionviajes.core.designsystem.component.AppButtonSize
import com.gestionviajes.core.designsystem.component.AppButtonStyle
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppIconBadge
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.presentation.TripUiState
import com.gestionviajes.feature.trips.presentation.TripViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

private val SpanishLocale = Locale.forLanguageTag("es-ES")
private val TourDateFormatter = DateTimeFormatter.ofPattern("dd MMM uuuu", SpanishLocale)
private val TourTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", SpanishLocale)

@Composable
fun TripListScreen(
    onCreateTour: () -> Unit,
    onOpenTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onOpenReports: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: TripViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripListContent(
        uiState = uiState,
        onCreateTour = onCreateTour,
        onOpenTour = onOpenTour,
        onEditTour = onEditTour,
        onOpenReports = onOpenReports,
        onOpenHistory = onOpenHistory,
    )
}

@Composable
private fun TripListContent(
    uiState: TripUiState,
    onCreateTour: () -> Unit,
    onOpenTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onOpenReports: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val tourCount = uiState.trips.size
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

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(SpanishLocale),
        title = stringResource(R.string.tours_title),
        subtitle = pluralStringResource(
            R.plurals.registered_tour_count,
            tourCount,
            tourCount,
        ),
        headerIcon = Icons.Rounded.DirectionsBus,
        bottomBar = {
            AppBottomNavigation(
                items = navigationItems,
                selectedIndex = 1,
                onItemSelected = { index ->
                    when (index) {
                        0 -> onOpenReports()
                        2 -> onOpenHistory()
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
            contentPadding = PaddingValues(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            if (uiState.errorMessage != null) {
                item {
                    TourLoadError()
                }
            }

            when {
                uiState.isLoading && uiState.trips.isEmpty() -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.extraLarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.trips.isEmpty() && uiState.errorMessage == null -> item {
                    AppEmptyState(
                        title = stringResource(R.string.empty_tours_title),
                        message = stringResource(R.string.empty_tours_message),
                        icon = Icons.Rounded.DirectionsBus,
                    )
                }

                else -> item {
                    TourCarousel(
                        trips = uiState.trips,
                        onOpenTour = onOpenTour,
                        onEditTour = onEditTour,
                    )
                }
            }

            item {
                AppButton(
                    text = stringResource(R.string.create_tour_action),
                    onClick = onCreateTour,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TourCarousel(
    trips: List<Trip>,
    onOpenTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
) {
    val spacing = AppTheme.spacing
    val pagerState = rememberPagerState(pageCount = { trips.size })

    LaunchedEffect(trips.size) {
        if (trips.isNotEmpty() && pagerState.currentPage > trips.lastIndex) {
            pagerState.scrollToPage(trips.lastIndex)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.large),
                pageSize = PageSize.Fixed(maxWidth - (spacing.large * 2f)),
                pageSpacing = spacing.medium,
                key = { page -> trips.getOrNull(page)?.id ?: page },
            ) { page ->
                val trip = trips.getOrNull(page)
                if (trip != null) {
                TourCard(
                    trip = trip,
                    onOpen = { onOpenTour(trip.id) },
                    secondaryActionText = stringResource(R.string.manage_tour_action),
                    onSecondaryAction = { onEditTour(trip.id) },
                        modifier = Modifier.padding(vertical = spacing.small),
                    )
                }
            }
        }

        TourPagerIndicator(pagerState = pagerState)
    }
}

@Composable
private fun TourPagerIndicator(pagerState: PagerState) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val scope = rememberCoroutineScope()
    val indicatorState = rememberLazyListState()

    LaunchedEffect(pagerState.currentPage, pagerState.pageCount) {
        if (pagerState.pageCount > 0) {
            indicatorState.animateScrollToItem(
                pagerState.currentPage.coerceIn(0, pagerState.pageCount - 1),
            )
        }
    }

    LazyRow(
        state = indicatorState,
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        contentPadding = PaddingValues(horizontal = spacing.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(
            count = pagerState.pageCount,
            key = { page -> page },
        ) { page ->
            val selected = pagerState.currentPage == page
            val pageDescription = stringResource(
                R.string.tour_page_indicator,
                page + 1,
                pagerState.pageCount,
            )

            Box(
                modifier = Modifier
                    .widthIn(min = dimensions.minimumTouchTarget)
                    .heightIn(min = dimensions.minimumTouchTarget)
                    .clip(CircleShape)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                    ) {
                        scope.launch { pagerState.animateScrollToPage(page) }
                    }
                    .semantics { contentDescription = pageDescription },
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Surface(
                        modifier = Modifier
                            .widthIn(min = dimensions.pagerIndicator)
                            .heightIn(min = dimensions.pagerIndicator),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shadowElevation = dimensions.buttonElevation,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (page + 1).toString(),
                                modifier = Modifier.padding(horizontal = spacing.small),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(dimensions.pagerDot)
                            .background(
                                color = MaterialTheme.colorScheme.outline,
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TourLoadError() {
    val spacing = AppTheme.spacing

    Surface(
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = AppTheme.dimensions.cardElevation,
    ) {
        Row(
            modifier = Modifier.padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimensions.iconMedium),
            )
            Text(
                text = stringResource(R.string.tours_load_error),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
internal fun TourCard(
    trip: Trip,
    onOpen: () -> Unit,
    secondaryActionText: String,
    onSecondaryAction: () -> Unit,
    secondaryActionFullWidth: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val colors = AppTheme.extendedColors
    val departure = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())
    val dateLabel = TourDateFormatter.format(departure).uppercase(SpanishLocale)
    val timeLabel = TourTimeFormatter.format(departure)
    val destination = trip.destination?.trim()?.takeIf(String::isNotEmpty)
    val supervisor = trip.supervisor?.trim()?.takeIf(String::isNotEmpty)
    val destinationLabel = destination ?: stringResource(R.string.tour_destination_unknown)
    val supervisorLabel = if (supervisor != null) {
        stringResource(R.string.tour_supervisor_value, supervisor)
    } else {
        stringResource(R.string.tour_supervisor_unknown)
    }

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                AppIconBadge(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    containerColor = colors.accentSkyContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                ) {
                    Text(
                        text = destinationLabel,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = supervisorLabel,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = spacing.medium,
                        vertical = spacing.small,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconMedium),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    ) {
                        Text(text = dateLabel, style = MaterialTheme.typography.titleMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(dimensions.iconMedium),
                            )
                            Text(text = timeLabel, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                color = colors.accentGreenContainer,
                contentColor = colors.onSuccessContainer,
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = spacing.medium,
                        vertical = spacing.small,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EventSeat,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.iconMedium),
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.tour_seat_count,
                            trip.seatCount,
                            trip.seatCount,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            if (secondaryActionFullWidth) {
                AppButton(
                    text = stringResource(R.string.open_tour_action),
                    onClick = onOpen,
                    modifier = Modifier.fillMaxWidth(),
                    size = AppButtonSize.Compact,
                )
                AppButton(
                    text = secondaryActionText,
                    onClick = onSecondaryAction,
                    modifier = Modifier.fillMaxWidth(),
                    style = AppButtonStyle.Outlined,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    AppButton(
                        text = stringResource(R.string.open_tour_action),
                        onClick = onOpen,
                        modifier = Modifier.weight(1f),
                        size = AppButtonSize.Compact,
                    )
                    AppButton(
                        text = secondaryActionText,
                        onClick = onSecondaryAction,
                        modifier = Modifier.weight(1f),
                        style = AppButtonStyle.Outlined,
                        size = AppButtonSize.Compact,
                    )
                }
            }
        }
    }
}
