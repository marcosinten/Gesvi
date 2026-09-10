package com.gestionviajes.feature.reports.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppButton
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppIconBadge
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.domain.model.TourDetail
import java.io.File
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.launch

/**
 * Informe de viaje: selección de viaje mediante tarjetas deslizables con indicador
 * (* * 2 * *), y visualización del listado de asientos con duplicación según tramos
 * (Solo ida / Solo venida) junto con el botón de generar PDF para el viaje seleccionado.
 */
@Composable
fun TripReportScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit = {},
    viewModel: ReportsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val manifestDetails by viewModel.allTourDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var generating by remember { mutableStateOf(false) }
    var generatingTickets by remember { mutableStateOf(false) }
    val spacing = AppTheme.spacing

    val title = stringResource(R.string.trip_report_title)
    val subtitle = stringResource(R.string.trip_report_subtitle)

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(ReportsLocale),
        title = title,
        subtitle = subtitle,
        headerIcon = Icons.Rounded.Assessment,
        onNavigateBack = onNavigateBack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ReportsBottomNavigation(
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = onNavigateToHistory,
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading && manifestDetails.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            manifestDetails.isEmpty() && uiState.trips.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(spacing.large),
                contentAlignment = Alignment.Center,
            ) {
                AppEmptyState(
                    title = stringResource(R.string.reports_no_tours_title),
                    message = stringResource(R.string.reports_no_tours_message),
                    icon = Icons.Rounded.Assessment,
                )
            }

            else -> {
                val groupedDetails = remember(manifestDetails) {
                    manifestDetails.sortedBy { detail -> detail.trip.dateMillis }
                }
                val pagerState = rememberPagerState(pageCount = { groupedDetails.size })
                LaunchedEffect(groupedDetails.size) {
                    if (groupedDetails.isNotEmpty() && pagerState.currentPage > groupedDetails.lastIndex) {
                        pagerState.scrollToPage(groupedDetails.lastIndex)
                    }
                }

                val selectedDetail = groupedDetails.getOrNull(pagerState.currentPage)
                    ?: groupedDetails.firstOrNull()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(vertical = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                ) {
                    item(key = "trip-report-pager") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.small),
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                contentPadding = PaddingValues(horizontal = spacing.large),
                                pageSpacing = spacing.medium,
                                pageSize = PageSize.Fill,
                                modifier = Modifier.fillMaxWidth(),
                            ) { page ->
                                val detail = groupedDetails[page]
                                TripReportCard(detail = detail)
                            }

                            if (groupedDetails.size > 1) {
                                TourPagerIndicator(pagerState = pagerState)
                            }
                        }
                    }

                    if (selectedDetail != null) {
                        item(key = "trip-report-pdf-${selectedDetail.trip.id}") {
                            AppButton(
                                text = stringResource(
                                    if (generating) R.string.generating_pdf_action else R.string.generate_pdf_action,
                                ),
                                enabled = !generating,
                                onClick = {
                                    scope.launch {
                                        generating = true
                                        runCatching {
                                            val file = ReportPdfExporter.createTripManifest(context, selectedDetail)
                                            sharePdf(context, file, context.getString(R.string.share_trip_report_title))
                                        }.onFailure {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.report_pdf_share_error),
                                            )
                                        }
                                        generating = false
                                    }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.large),
                            )
                        }

                        item(key = "trip-report-tickets-${selectedDetail.trip.id}") {
                            AppButton(
                                text = stringResource(
                                    if (generatingTickets) R.string.generating_pdf_action else R.string.print_tickets_action,
                                ),
                                enabled = !generatingTickets,
                                onClick = {
                                    scope.launch {
                                        generatingTickets = true
                                        runCatching {
                                            val file = TicketPdfExporter.createTickets(context, selectedDetail)
                                            sharePdf(context, file, context.getString(R.string.share_tickets_title))
                                        }.onFailure {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.report_pdf_share_error),
                                            )
                                        }
                                        generatingTickets = false
                                    }
                                },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Rounded.Print, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.large),
                            )
                        }

                        item(key = "trip-report-manifest-${selectedDetail.trip.id}") {
                            val rows = remember(selectedDetail) { buildTripManifestRows(selectedDetail) }
                            TripManifestContentCard(
                                detail = selectedDetail,
                                rows = rows,
                                modifier = Modifier.padding(horizontal = spacing.large),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripReportCard(
    detail: TourDetail,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val colors = AppTheme.extendedColors
    val trip = detail.trip
    val departure = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())
    val destination = trip.destination?.trim()?.takeIf(String::isNotEmpty)
        ?: stringResource(R.string.tour_destination_unknown)
    val supervisor = trip.supervisor?.trim()?.takeIf(String::isNotEmpty)
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
                    imageVector = Icons.Rounded.DirectionsBus,
                    contentDescription = null,
                    containerColor = colors.accentSkyContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                ) {
                    Text(
                        text = destination,
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
                    Text(
                        text = "${ReportsDateFormatter.format(departure).uppercase(ReportsLocale)} · ${ReportsTimeFormatter.format(departure)}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
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
private fun TripManifestContentCard(
    detail: TourDetail,
    rows: List<ManifestSeatRow>,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val departure = Instant.ofEpochMilli(detail.trip.dateMillis).atZone(ZoneId.systemDefault())

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                Text(
                    text = detail.trip.destination.orEmpty(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "${ReportsDateFormatter.format(departure)} · ${ReportsTimeFormatter.format(departure)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (rows.isEmpty()) {
                Text(
                    text = stringResource(R.string.passenger_report_empty_message),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                ManifestHeaderRow()
                rows.forEach { row -> ManifestSeatRow(row = row) }
            }
        }
    }
}

@Composable
private fun ManifestHeaderRow() {
    val spacing = AppTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.report_seat_number_label),
            modifier = Modifier.width(AppTheme.dimensions.iconMedium * 2),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = stringResource(R.string.report_name_label),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = stringResource(R.string.report_abono_label),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun ManifestSeatRow(row: ManifestSeatRow) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.dimensions.minimumTouchTarget),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = row.seatNumber.toString().padStart(2, '0'),
            modifier = Modifier.width(AppTheme.dimensions.iconMedium * 2),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            Text(
                text = row.fareType?.let { fareType -> fareLabel(fareType) } ?: "-",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = row.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = row.abonoCents?.let { formatMoney(it, currencySymbol) } ?: "-",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private fun sharePdf(context: Context, file: File, title: String) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, title))
}