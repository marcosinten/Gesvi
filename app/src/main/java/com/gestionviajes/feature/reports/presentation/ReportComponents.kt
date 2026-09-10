package com.gestionviajes.feature.reports.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppBottomNavigation
import com.gestionviajes.core.designsystem.component.AppBottomNavigationItem
import com.gestionviajes.core.designsystem.component.AppButton
import com.gestionviajes.core.designsystem.component.AppButtonStyle
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppHeroHeader
import com.gestionviajes.core.designsystem.component.StatusBadge
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.TourBookingSummary
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.model.Trip
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val ReportsLocale = Locale.forLanguageTag("es-ES")
internal val ReportsDateFormatter = DateTimeFormatter.ofPattern("dd MMM uuuu", ReportsLocale)
internal val ReportsTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", ReportsLocale)

@Composable
internal fun ReportsBottomNavigation(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
) {
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
    AppBottomNavigation(
        items = navigationItems,
        selectedIndex = 0,
        onItemSelected = { index ->
            when (index) {
                1 -> onNavigateToHome()
                2 -> onNavigateToHistory()
                else -> Unit
            }
        },
    )
}

/**
 * Contenido compartido de los informes de pasajeros. Deriva siempre del estado
 * del repositorio/dominio expuesto en [uiState]; el filtro de [bookingFilter]
 * solo adapta qué bookings del mismo [TourDetail] se muestran (proyección
 * descartable, nunca una copia autoritativa de datos).
 */
@Composable
internal fun ReportContent(
    uiState: ReportsUiState,
    onTourSelected: (Long) -> Unit,
    title: String,
    subtitle: String,
    bookingFilter: (TourDetail) -> List<TourBookingSummary>,
    modifier: Modifier = Modifier,
    showHeroHeader: Boolean = true,
) {
    when {
        uiState.isLoading -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        uiState.trips.isEmpty() -> Box(
            modifier = modifier.padding(AppTheme.spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            AppEmptyState(
                title = stringResource(R.string.reports_no_tours_title),
                message = stringResource(R.string.reports_no_tours_message),
                icon = Icons.Rounded.Assessment,
            )
        }

        uiState.loadFailed || uiState.detail == null -> Box(
            modifier = modifier.padding(AppTheme.spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            AppEmptyState(
                title = stringResource(R.string.reports_load_error_title),
                message = stringResource(R.string.reports_load_error_message),
                icon = Icons.Rounded.Assessment,
            )
        }

        else -> {
            val detail = checkNotNull(uiState.detail)
            PassengerReport(
                trips = uiState.trips,
                selectedTourId = uiState.selectedTourId!!,
                detail = detail,
                bookings = bookingFilter(detail),
                onTourSelected = onTourSelected,
                title = title,
                subtitle = subtitle,
                showHeroHeader = showHeroHeader,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun PassengerReport(
    trips: List<Trip>,
    selectedTourId: Long,
    detail: TourDetail,
    bookings: List<TourBookingSummary>,
    onTourSelected: (Long) -> Unit,
    title: String,
    subtitle: String,
    showHeroHeader: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        if (showHeroHeader) {
            item(key = "reports-header") {
                AppHeroHeader(
                    title = title,
                    icon = Icons.Rounded.Assessment,
                    eyebrow = stringResource(R.string.app_name).uppercase(ReportsLocale),
                    subtitle = subtitle,
                )
            }
        }
        if (trips.size > 1) {
            item {
                TourSelector(
                    trips = trips,
                    selectedTourId = selectedTourId,
                    onTourSelected = onTourSelected,
                    modifier = Modifier.padding(horizontal = spacing.large),
                )
            }
        }
        item {
            TourReportSummary(
                trip = detail.trip,
                modifier = Modifier.padding(horizontal = spacing.large),
            )
        }
        item {
            Text(
                text = stringResource(R.string.passenger_report_list_title),
                modifier = Modifier.padding(horizontal = spacing.large),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        if (bookings.isEmpty()) {
            item {
                AppEmptyState(
                    title = stringResource(R.string.passenger_report_empty_title),
                    message = stringResource(R.string.passenger_report_empty_message),
                    icon = Icons.Rounded.Assessment,
                    modifier = Modifier.padding(horizontal = spacing.large),
                )
            }
        } else {
            items(
                items = bookings.sortedBy { booking -> booking.seats.minOfOrNull { seat -> seat.number } },
                key = TourBookingSummary::id,
            ) { booking ->
                PassengerGroupCard(
                    booking = booking,
                    modifier = Modifier.padding(horizontal = spacing.large),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TourSelector(
    trips: List<Trip>,
    selectedTourId: Long,
    onTourSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTrip = trips.first { trip -> trip.id == selectedTourId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = tourSelectorLabel(selectedTrip),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            label = { Text(stringResource(R.string.report_tour_selector_label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            trips.forEach { trip ->
                DropdownMenuItem(
                    text = { Text(tourSelectorLabel(trip)) },
                    onClick = {
                        onTourSelected(trip.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
internal fun TourReportSummary(trip: Trip, modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing
    val departure = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            ReportField(
                label = stringResource(R.string.report_destination_label),
                value = trip.destination.orEmpty(),
            )
            ReportField(
                label = stringResource(R.string.report_date_label),
                value = ReportsDateFormatter.format(departure).uppercase(ReportsLocale),
            )
            ReportField(
                label = stringResource(R.string.report_time_label),
                value = ReportsTimeFormatter.format(departure),
            )
            ReportField(
                label = stringResource(R.string.report_supervisor_label),
                value = trip.supervisor.orEmpty(),
            )
        }
    }
}

@Composable
internal fun PassengerGroupCard(
    booking: TourBookingSummary,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    var paymentsExpanded by rememberSaveable(booking.id) { mutableStateOf(false) }
    val currencySymbol = stringResource(R.string.currency_symbol)

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = booking.responsibleName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                )
                StatusBadge(
                    text = statusLabel(booking.status),
                )
            }
            ReportField(
                label = stringResource(R.string.report_seat_number_label),
                value = booking.seats.sortedBy { seat -> seat.number }
                    .joinToString { seat -> seat.number.toString() },
            )
            ReportField(
                label = stringResource(R.string.report_fare_type_label),
                value = booking.seats.sortedBy { seat -> seat.number }
                    .map { seat -> "${seat.number}: ${fareLabel(seat.fareType)}" }
                    .joinToString(),
            )
            ReportField(
                label = stringResource(R.string.report_name_label),
                value = booking.responsibleName,
            )
            ReportField(
                label = stringResource(R.string.report_status_label),
                value = statusLabel(booking.status),
            )
            MoneyReportField(
                label = stringResource(R.string.report_total_label),
                cents = booking.totalCents,
                currencySymbol = currencySymbol,
            )
            MoneyReportField(
                label = stringResource(R.string.report_paid_label),
                cents = booking.receivedCents,
                currencySymbol = currencySymbol,
            )
            MoneyReportField(
                label = stringResource(R.string.report_balance_label),
                cents = booking.pendingCents,
                currencySymbol = currencySymbol,
            )
            AppButton(
                text = stringResource(
                    if (paymentsExpanded) R.string.hide_payments_action else R.string.show_payments_action,
                ),
                onClick = { paymentsExpanded = !paymentsExpanded },
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Outlined,
                leadingIcon = {
                    Icon(
                        imageVector = if (paymentsExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                    )
                },
            )
            if (paymentsExpanded) {
                PaymentHistory(
                    booking = booking,
                    currencySymbol = currencySymbol,
                )
            }
        }
    }
}

@Composable
private fun PaymentHistory(booking: TourBookingSummary, currencySymbol: String) {
    val spacing = AppTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text(
            text = stringResource(R.string.payment_history_title),
            style = MaterialTheme.typography.titleMedium,
        )
        if (booking.payments.isEmpty()) {
            Text(
                text = stringResource(R.string.payment_history_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            booking.payments.forEachIndexed { index, payment ->
                val paymentDate = Instant.ofEpochMilli(payment.dateMillis)
                    .atZone(ZoneId.systemDefault())
                ReportField(
                    label = stringResource(R.string.payment_history_item, index + 1),
                    value = "${formatMoney(payment.amountCents, currencySymbol)} - " +
                        "${ReportsDateFormatter.format(paymentDate)} ${ReportsTimeFormatter.format(paymentDate)}",
                )
            }
        }
    }
}

@Composable
internal fun ReportField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
internal fun MoneyReportField(label: String, cents: Long, currencySymbol: String) {
    ReportField(label = label, value = formatMoney(cents, currencySymbol))
}

internal fun tourSelectorLabel(trip: Trip): String {
    val date = Instant.ofEpochMilli(trip.dateMillis).atZone(ZoneId.systemDefault())
    return "${trip.destination.orEmpty()} - ${ReportsDateFormatter.format(date)}"
}

@Composable
internal fun fareLabel(fareType: FareType): String = when (fareType) {
    FareType.ROUND_TRIP -> stringResource(R.string.round_trip_fare_label)
    FareType.OUTBOUND -> stringResource(R.string.outbound_fare_label)
    FareType.RETURN -> stringResource(R.string.return_fare_label)
}

@Composable
internal fun statusLabel(status: TourSeatStatus): String = when (status) {
    TourSeatStatus.EMPTY -> stringResource(R.string.seat_status_empty)
    TourSeatStatus.RESERVED -> stringResource(R.string.seat_status_reserved)
    TourSeatStatus.PARTIAL -> stringResource(R.string.seat_status_partial)
    TourSeatStatus.PAID -> stringResource(R.string.seat_status_paid)
}

internal fun formatMoney(cents: Long, currencySymbol: String): String =
    "$currencySymbol${String.format(ReportsLocale, "%d.%02d", cents / 100, cents % 100)}"