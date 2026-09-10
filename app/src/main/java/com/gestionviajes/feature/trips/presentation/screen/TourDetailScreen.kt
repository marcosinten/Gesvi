package com.gestionviajes.feature.trips.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppButton
import com.gestionviajes.core.designsystem.component.AppButtonStyle
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppHeroHeader
import com.gestionviajes.core.designsystem.component.AppIconBadge
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.component.SeatCell
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.core.designsystem.token.SeatStatus
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourBookingSummary
import com.gestionviajes.feature.trips.domain.model.TourAccountsSummary
import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.presentation.BusSeatRow
import com.gestionviajes.feature.trips.presentation.STANDARD_BUS_ROW_SIZE
import com.gestionviajes.feature.trips.presentation.TourDetailEffect
import com.gestionviajes.feature.trips.presentation.TourDetailEvent
import com.gestionviajes.feature.trips.presentation.TourDetailUiState
import com.gestionviajes.feature.trips.presentation.TourDetailViewModel
import com.gestionviajes.feature.trips.presentation.parseMoneyToCents
import com.gestionviajes.feature.trips.presentation.UnavailableTourAction
import com.gestionviajes.feature.trips.presentation.buildBusSeatRows
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TourDetailLocale = Locale.forLanguageTag("es-ES")
private val TourDetailDateTimeFormatter = DateTimeFormatter.ofPattern(
    "dd MMM uuuu · HH:mm",
    TourDetailLocale,
)

@Composable
fun TourDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TourDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val reserveUnavailable = stringResource(R.string.reserve_form_unavailable)
    val sellUnavailable = stringResource(R.string.sell_form_unavailable)
    val managementUnavailable = stringResource(R.string.booking_management_unavailable)
    val currentMessages by rememberUpdatedState(
        mapOf(
            UnavailableTourAction.Reserve to reserveUnavailable,
            UnavailableTourAction.Sell to sellUnavailable,
            UnavailableTourAction.BookingManagement to managementUnavailable,
        ),
    )

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TourDetailEffect.ShowUnavailable -> {
                    currentMessages[effect.action]?.let { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }
            }
        }
    }

    TourDetailContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onEvent = viewModel::onEvent,
    )
    if (uiState.selling && uiState.detail != null) {
        SaleDialog(uiState, viewModel::onEvent)
    }
    if (uiState.reserving && uiState.detail != null) {
        ReservationDialog(uiState, viewModel::onEvent)
    }
    uiState.selectedBooking?.let { booking ->
        if (uiState.editingBooking) {
            EditBookingSheet(
                booking = booking,
                trip = uiState.detail!!.trip,
                changingFareSeatNumber = uiState.changingFareSeatNumber,
                savingFareChange = uiState.savingFareChange,
                fareChangeFailed = uiState.fareChangeFailed,
                releasingSeatNumber = uiState.releasingSeatNumber,
                releasingSeat = uiState.releasingSeat,
                releaseSeatFailed = uiState.releaseSeatFailed,
                onDismiss = { viewModel.onEvent(TourDetailEvent.DismissBookingEditor) },
                onChangeFare = { seatNumber ->
                    viewModel.onEvent(TourDetailEvent.ChangeFareRequested(seatNumber))
                },
                onFareSelected = { fareType ->
                    viewModel.onEvent(TourDetailEvent.FareChangeSelected(fareType))
                },
                onDismissFareChange = {
                    viewModel.onEvent(TourDetailEvent.DismissFareChange)
                },
                onReleaseSeat = { seatNumber ->
                    viewModel.onEvent(TourDetailEvent.ReleaseSeatRequested(seatNumber))
                },
                onConfirmRelease = {
                    viewModel.onEvent(TourDetailEvent.ConfirmSeatRelease)
                },
                onDismissRelease = {
                    viewModel.onEvent(TourDetailEvent.DismissSeatRelease)
                },
                onChangeSeat = { seatNumber ->
                    viewModel.onEvent(TourDetailEvent.ChangeSeatRequested(seatNumber))
                },
            )
        } else if (uiState.registeringPayment) {
            RegisterPaymentSheet(
                booking = booking,
                amount = uiState.paymentAmount,
                amountHasError = uiState.paymentAmountHasError,
                saveFailed = uiState.paymentSaveFailed,
                saving = uiState.savingPayment,
                onAmountChanged = { value ->
                    viewModel.onEvent(TourDetailEvent.PaymentAmountChanged(value))
                },
                onDismiss = { viewModel.onEvent(TourDetailEvent.DismissPayment) },
                onConfirm = { viewModel.onEvent(TourDetailEvent.ConfirmPayment) },
            )
        } else {
            BookingSummarySheet(
                booking = booking,
                onDismiss = { viewModel.onEvent(TourDetailEvent.DismissSelectedBooking) },
                onManage = { viewModel.onEvent(TourDetailEvent.ManageSelectedBooking) },
                onRegisterPayment = {
                    viewModel.onEvent(TourDetailEvent.RegisterPaymentRequested)
                },
                onAddSeats = {
                    viewModel.onEvent(TourDetailEvent.AddSeatsRequested)
                },
                onDeleteBooking = {
                    viewModel.onEvent(TourDetailEvent.DeleteBookingRequested)
                },
            )
        }
    }
    val changingSeatNumber = uiState.changingSeatNumber
    val targetSeatNumber = uiState.targetSeatNumber
    if (changingSeatNumber != null && targetSeatNumber != null) {
        ConfirmSeatMoveDialog(
            fromSeatNumber = changingSeatNumber,
            toSeatNumber = targetSeatNumber,
            moving = uiState.movingSeat,
            moveFailed = uiState.moveSeatFailed,
            onConfirm = { viewModel.onEvent(TourDetailEvent.ConfirmSeatMove) },
            onDismiss = { viewModel.onEvent(TourDetailEvent.DismissSeatMove) },
        )
    }
    if (uiState.showDeleteBookingDialog) {
        DeleteBookingDialog(
            deleting = uiState.deletingBooking,
            deleteFailed = uiState.deleteBookingFailed,
            onConfirm = { viewModel.onEvent(TourDetailEvent.ConfirmDeleteBooking) },
            onDismiss = { viewModel.onEvent(TourDetailEvent.DismissDeleteBooking) },
        )
    }
    val currentDetail = uiState.detail
    if (uiState.reviewingAddedSeats && currentDetail != null) {
        AddSeatsReviewSheet(
            responsibleName = uiState.addingSeatsResponsibleName,
            selectedSeatNumbers = uiState.selectedSeatNumbers,
            seatFares = uiState.addedSeatFares,
            trip = currentDetail.trip,
            saving = uiState.savingAddedSeats,
            saveFailed = uiState.addSeatsFailed,
            onFareSelected = { seatNumber, fareType ->
                viewModel.onEvent(TourDetailEvent.AddedSeatFareChanged(seatNumber, fareType))
            },
            onConfirm = { viewModel.onEvent(TourDetailEvent.ConfirmAddedSeats) },
            onDismiss = { viewModel.onEvent(TourDetailEvent.DismissAddedSeatsReview) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingSummarySheet(
    booking: TourBookingSummary,
    onDismiss: () -> Unit,
    onManage: () -> Unit,
    onRegisterPayment: () -> Unit,
    onAddSeats: () -> Unit,
    onDeleteBooking: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.large, vertical = spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.booking_summary_title),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_booking_summary),
                    )
                }
            }

            BookingSummaryField(
                label = stringResource(R.string.booking_responsible_label),
                value = booking.responsibleName,
            )
            BookingSummaryField(
                label = stringResource(R.string.booking_seats_label),
                value = booking.seats
                    .sortedBy { seat -> seat.number }
                    .joinToString { seat -> seat.number.toString() },
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                for (seat in booking.seats.sortedBy { it.number }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${stringResource(R.string.seat_label)} ${seat.number}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = fareLabel(seat.fareType),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = formatMoney(seat.fareCents, currencySymbol),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            MoneySummaryRow(
                label = stringResource(R.string.sale_total_label),
                amount = formatMoney(booking.totalCents, currencySymbol),
            )
            MoneySummaryRow(
                label = stringResource(R.string.booking_paid_label),
                amount = formatMoney(booking.receivedCents, currencySymbol),
            )
            if (booking.creditCents > 0) {
                MoneySummaryRow(
                    label = stringResource(R.string.sale_credit_label),
                    amount = formatMoney(booking.creditCents, currencySymbol),
                )
            } else {
                MoneySummaryRow(
                    label = stringResource(R.string.sale_pending_label),
                    amount = formatMoney(booking.pendingCents, currencySymbol),
                )
            }

            AppButton(
                text = stringResource(R.string.add_seats_action),
                onClick = onAddSeats,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(R.string.manage_booking_action),
                onClick = onManage,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(R.string.register_payment_action),
                onClick = onRegisterPayment,
                modifier = Modifier.fillMaxWidth(),
            )
            if (booking.status == TourSeatStatus.RESERVED) {
                AppButton(
                    text = stringResource(R.string.delete_booking_action),
                    onClick = onDeleteBooking,
                    modifier = Modifier.fillMaxWidth(),
                    style = AppButtonStyle.Outlined,
                )
            }
            Spacer(modifier = Modifier.height(spacing.small))
        }
    }
}

@Composable
private fun DeleteBookingDialog(
    deleting: Boolean,
    deleteFailed: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = AppTheme.spacing
    AlertDialog(
        onDismissRequest = { if (!deleting) onDismiss() },
        title = { Text(stringResource(R.string.delete_booking_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Text(stringResource(R.string.delete_booking_message))
                if (deleteFailed) {
                    Text(
                        text = stringResource(R.string.delete_booking_error),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !deleting) {
                Text(
                    if (deleting) {
                        stringResource(R.string.deleting_booking_action)
                    } else {
                        stringResource(R.string.delete_action)
                    },
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !deleting) {
                Text(stringResource(R.string.cancel_action))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBookingSheet(
    booking: TourBookingSummary,
    trip: Trip,
    changingFareSeatNumber: Int?,
    savingFareChange: Boolean,
    fareChangeFailed: Boolean,
    releasingSeatNumber: Int?,
    releasingSeat: Boolean,
    releaseSeatFailed: Boolean,
    onDismiss: () -> Unit,
    onChangeFare: (Int) -> Unit,
    onFareSelected: (com.gestionviajes.feature.trips.domain.model.FareType) -> Unit,
    onDismissFareChange: () -> Unit,
    onReleaseSeat: (Int) -> Unit,
    onConfirmRelease: () -> Unit,
    onDismissRelease: () -> Unit,
    onChangeSeat: (Int) -> Unit,
) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)

    ModalBottomSheet(onDismissRequest = { if (!savingFareChange && !releasingSeat) onDismiss() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.large, vertical = spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.manage_booking_action),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(onClick = onDismiss, enabled = !savingFareChange && !releasingSeat) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_booking_editor),
                    )
                }
            }

            BookingSummaryField(
                label = stringResource(R.string.booking_responsible_label),
                value = booking.responsibleName,
            )
            BookingSummaryField(
                label = stringResource(R.string.booking_seats_label),
                value = booking.seats
                    .sortedBy { seat -> seat.number }
                    .joinToString { seat -> seat.number.toString() },
            )
            
            Text(
                text = stringResource(R.string.edit_seats_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                for (seat in booking.seats.sortedBy { it.number }) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(spacing.small),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${stringResource(R.string.seat_label)} ${seat.number}",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = fareLabel(seat.fareType),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Text(
                                        text = formatMoney(seat.fareCents, currencySymbol),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                            AppButton(
                                text = stringResource(R.string.change_fare_action),
                                onClick = { onChangeFare(seat.number) },
                                modifier = Modifier.fillMaxWidth(),
                                style = AppButtonStyle.Outlined,
                                enabled = !savingFareChange && !releasingSeat,
                            )
                            AppButton(
                                text = stringResource(R.string.change_seat_action),
                                onClick = { onChangeSeat(seat.number) },
                                modifier = Modifier.fillMaxWidth(),
                                style = AppButtonStyle.Outlined,
                                enabled = !savingFareChange && !releasingSeat,
                            )
                            AppButton(
                                text = stringResource(R.string.release_seat_action),
                                onClick = { onReleaseSeat(seat.number) },
                                modifier = Modifier.fillMaxWidth(),
                                style = AppButtonStyle.Outlined,
                                enabled = !savingFareChange && !releasingSeat,
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            MoneySummaryRow(
                label = stringResource(R.string.sale_total_label),
                amount = formatMoney(booking.totalCents, currencySymbol),
            )
            MoneySummaryRow(
                label = stringResource(R.string.booking_paid_label),
                amount = formatMoney(booking.receivedCents, currencySymbol),
            )
            if (booking.creditCents > 0) {
                MoneySummaryRow(
                    label = stringResource(R.string.sale_credit_label),
                    amount = formatMoney(booking.creditCents, currencySymbol),
                )
            } else {
                MoneySummaryRow(
                    label = stringResource(R.string.sale_pending_label),
                    amount = formatMoney(booking.pendingCents, currencySymbol),
                )
            }
            
            AppButton(
                text = stringResource(R.string.close_action),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                enabled = !savingFareChange && !releasingSeat,
            )
            Spacer(modifier = Modifier.height(spacing.small))
        }

        if (releasingSeatNumber != null) {
            AlertDialog(
                onDismissRequest = { if (!releasingSeat) onDismissRelease() },
                title = { Text(stringResource(R.string.release_seat_title, releasingSeatNumber)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Text(stringResource(R.string.release_seat_message))
                        if (releaseSeatFailed) {
                            Text(
                                text = stringResource(R.string.release_seat_error),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onConfirmRelease, enabled = !releasingSeat) {
                        if (releasingSeat) {
                            Text(stringResource(R.string.releasing_seat_action))
                        } else {
                            Text(stringResource(R.string.confirm_action))
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissRelease, enabled = !releasingSeat) {
                        Text(stringResource(R.string.cancel_action))
                    }
                },
            )
        }
    }

    changingFareSeatNumber?.let { seatNumber ->
        val currentFare = booking.seats
            .firstOrNull { seat -> seat.number == seatNumber }
            ?.fareType
        if (currentFare != null) {
            FareTypeChangeDialog(
                seatNumber = seatNumber,
                currentFare = currentFare,
                trip = trip,
                saving = savingFareChange,
                saveFailed = fareChangeFailed,
                onFareSelected = onFareSelected,
                onDismiss = onDismissFareChange,
            )
        }
    }
}

@Composable
private fun FareTypeChangeDialog(
    seatNumber: Int,
    currentFare: com.gestionviajes.feature.trips.domain.model.FareType,
    trip: Trip,
    saving: Boolean,
    saveFailed: Boolean,
    onFareSelected: (com.gestionviajes.feature.trips.domain.model.FareType) -> Unit,
    onDismiss: () -> Unit,
) {
    val currencySymbol = stringResource(R.string.currency_symbol)
    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = {
            Text(stringResource(R.string.change_fare_title, seatNumber))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                Text(
                    text = stringResource(R.string.change_fare_help),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
                for (fareType in trip.activeFareTypes.sortedBy { it.ordinal }) {
                        val fareCents = checkNotNull(trip.fareCents(fareType))
                        val currentSuffix = if (fareType == currentFare) {
                            " · ${stringResource(R.string.current_fare_label)}"
                        } else {
                            ""
                        }
                        AppButton(
                            text = "${fareLabel(fareType)} · ${formatMoney(fareCents, currencySymbol)}$currentSuffix",
                            onClick = { onFareSelected(fareType) },
                            modifier = Modifier.fillMaxWidth(),
                            style = if (fareType == currentFare) {
                                AppButtonStyle.Tonal
                            } else {
                                AppButtonStyle.Outlined
                            },
                            enabled = !saving,
                        )
                    }
                if (saving) {
                    Text(
                        text = stringResource(R.string.saving_fare_change),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                if (saveFailed) {
                    Text(
                        text = stringResource(R.string.fare_change_save_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !saving) {
                Text(stringResource(R.string.cancel_action))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun RegisterPaymentSheet(
    booking: TourBookingSummary,
    amount: String,
    amountHasError: Boolean,
    saveFailed: Boolean,
    saving: Boolean,
    onAmountChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val paymentAreaRequester = remember { BringIntoViewRequester() }
    var amountFocused by remember { mutableStateOf(false) }

    LaunchedEffect(amountFocused) {
        if (amountFocused) paymentAreaRequester.bringIntoView()
    }

    ModalBottomSheet(
        onDismissRequest = { if (!saving) onDismiss() },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = spacing.large, vertical = spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.register_payment_title),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { heading() },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                )
                IconButton(onClick = onDismiss, enabled = !saving) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close_payment_form),
                    )
                }
            }

            BookingSummaryField(
                label = stringResource(R.string.booking_responsible_label),
                value = booking.responsibleName,
            )
            BookingSummaryField(
                label = stringResource(R.string.booking_seats_label),
                value = booking.seats
                    .sortedBy { seat -> seat.number }
                    .joinToString { seat -> seat.number.toString() },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(paymentAreaRequester),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                MoneySummaryRow(
                    label = stringResource(R.string.sale_total_label),
                    amount = formatMoney(booking.totalCents, currencySymbol),
                )
                MoneySummaryRow(
                    label = stringResource(R.string.booking_current_paid_label),
                    amount = formatMoney(booking.receivedCents, currencySymbol),
                )
                MoneySummaryRow(
                    label = stringResource(R.string.sale_pending_label),
                    amount = formatMoney(booking.pendingCents, currencySymbol),
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { amountFocused = it.isFocused },
                    enabled = !saving,
                    label = { Text(stringResource(R.string.payment_amount_label)) },
                    prefix = { Text(currencySymbol) },
                    isError = amountHasError,
                    supportingText = if (amountHasError) {
                        { Text(stringResource(R.string.payment_amount_error)) }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }

            if (saveFailed) {
                Text(
                    text = stringResource(R.string.payment_save_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            AppButton(
                text = if (saving) {
                    stringResource(R.string.saving_payment_action)
                } else {
                    stringResource(R.string.save_payment_action)
                },
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving,
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving,
            ) {
                Text(stringResource(R.string.cancel_action))
            }
            Spacer(modifier = Modifier.height(spacing.small))
        }
    }
}

@Composable
private fun BookingSummaryField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.extraSmall)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(text = value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun MoneySummaryRow(label: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Text(text = amount, style = MaterialTheme.typography.titleLarge)
    }
}

private fun formatMoney(cents: Long, currencySymbol: String): String =
    "$currencySymbol${String.format(TourDetailLocale, "%d.%02d", cents / 100, cents % 100)}"

@Composable
private fun ReservationDialog(uiState: TourDetailUiState, onEvent: (TourDetailEvent) -> Unit) {
    val trip = uiState.detail!!.trip
    val seats = uiState.selectedSeatNumbers.sorted()
    val active = trip.activeFareTypes.toList()
    AlertDialog(
        onDismissRequest = { onEvent(TourDetailEvent.DismissSale) },
        title = { Text(stringResource(R.string.reserve_action)) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            ) {
                OutlinedTextField(uiState.responsibleName, { onEvent(TourDetailEvent.ResponsibleChanged(it)) }, label = { Text(stringResource(R.string.sale_responsible_label)) }, modifier = Modifier.fillMaxWidth())
                for (number in seats) {
                    val selected = uiState.seatFares[number]
                    Text("${stringResource(R.string.seat_label)} $number", style = MaterialTheme.typography.titleMedium)
                    for (fare in active) {
                        val price = trip.fareCents(fare) ?: 0
                        AppButton("${fareLabel(fare)}  $${"%.2f".format(price / 100.0)}${if (selected == fare) " ✓" else ""}", { onEvent(TourDetailEvent.FareChanged(number, fare)) }, Modifier.fillMaxWidth(), if (selected == fare) AppButtonStyle.Primary else AppButtonStyle.Outlined)
                    }
                }
                val total = seats.sumOf { number -> trip.fareCents(uiState.seatFares[number] ?: active.first()) ?: 0 }
                Text("${stringResource(R.string.sale_total_label)} $${"%.2f".format(total / 100.0)}", style = MaterialTheme.typography.titleLarge)
            }
        },
        confirmButton = { AppButton(stringResource(R.string.reserve_action), { onEvent(TourDetailEvent.ConfirmReservation) }) },
        dismissButton = { TextButton({ onEvent(TourDetailEvent.DismissSale) }) { Text(stringResource(R.string.cancel_action)) } },
    )
}

@Composable
private fun SaleDialog(uiState: TourDetailUiState, onEvent: (TourDetailEvent) -> Unit) {
    val trip = uiState.detail!!.trip
    val seats = uiState.selectedSeatNumbers.sorted()
    val active = trip.activeFareTypes.toList()
    AlertDialog(
        onDismissRequest = { onEvent(TourDetailEvent.DismissSale) },
        title = { Text(stringResource(R.string.sell_action)) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            ) {
                OutlinedTextField(uiState.responsibleName, { onEvent(TourDetailEvent.ResponsibleChanged(it)) }, label = { Text(stringResource(R.string.sale_responsible_label)) }, modifier = Modifier.fillMaxWidth())
                for (number in seats) {
                    val selected = uiState.seatFares[number]
                    Text("${stringResource(R.string.seat_label)} $number", style = MaterialTheme.typography.titleMedium)
                    for (fare in active) {
                        val price = trip.fareCents(fare) ?: 0
                        AppButton(
                            text = "${fareLabel(fare)}  $${"%.2f".format(price / 100.0)}${if (selected == fare) " ✓" else ""}",
                            onClick = { onEvent(TourDetailEvent.FareChanged(number, fare)) },
                            modifier = Modifier.fillMaxWidth(),
                            style = if (selected == fare) AppButtonStyle.Primary else AppButtonStyle.Outlined,
                        )
                    }
                }
                val total = seats.sumOf { number -> trip.fareCents(uiState.seatFares[number] ?: active.first()) ?: 0 }
                Text("${stringResource(R.string.sale_total_label)} $${"%.2f".format(total / 100.0)}", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(uiState.receivedAmount, { onEvent(TourDetailEvent.ReceivedChanged(it)) }, label = { Text(stringResource(R.string.sale_received_label)) }, modifier = Modifier.fillMaxWidth())
                val received = parseMoneyToCents(uiState.receivedAmount) ?: 0
                Text("${stringResource(R.string.sale_pending_label)} $${"%.2f".format((total - received).coerceAtLeast(0) / 100.0)}")
                Text("${stringResource(R.string.sale_credit_label)} $${"%.2f".format((received - total).coerceAtLeast(0) / 100.0)}")
            }
        },
        confirmButton = { AppButton(stringResource(R.string.sell_action), { onEvent(TourDetailEvent.ConfirmSale) }) },
        dismissButton = { TextButton({ onEvent(TourDetailEvent.DismissSale) }) { Text(stringResource(R.string.cancel_action)) } },
    )
}

@Composable
private fun fareLabel(type: com.gestionviajes.feature.trips.domain.model.FareType): String = when (type) {
    com.gestionviajes.feature.trips.domain.model.FareType.ROUND_TRIP -> stringResource(R.string.round_trip_fare_label)
    com.gestionviajes.feature.trips.domain.model.FareType.OUTBOUND -> stringResource(R.string.outbound_fare_label)
    com.gestionviajes.feature.trips.domain.model.FareType.RETURN -> stringResource(R.string.return_fare_label)
}

@Composable
private fun TourDetailContent(
    uiState: TourDetailUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onEvent: (TourDetailEvent) -> Unit,
) {
    val detail = uiState.detail
    val destination = detail?.trip?.destination?.trim()?.takeIf(String::isNotEmpty)
    val title = when {
        destination != null -> destination
        detail != null -> stringResource(R.string.tour_destination_unknown)
        else -> stringResource(R.string.tour_detail_title)
    }
    val dateTime = detail?.let {
        TourDetailDateTimeFormatter.format(
            Instant.ofEpochMilli(it.trip.dateMillis).atZone(ZoneId.systemDefault()),
        )
    }
    val showsScrollableDetail = detail != null &&
        !uiState.isLoading &&
        !uiState.loadFailed &&
        !uiState.notFound

    AppScreenScaffold(
        eyebrow = stringResource(R.string.tour_detail_eyebrow),
        title = title,
        subtitle = dateTime,
        headerIcon = Icons.Rounded.DirectionsBus,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
        showHeader = !showsScrollableDetail,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (uiState.changingSeatNumber != null) {
                ChangeSeatBanner(
                    fromSeatNumber = uiState.changingSeatNumber,
                    onCancel = { onEvent(TourDetailEvent.DismissSeatMove) },
                )
            } else if (uiState.addingSeats) {
                AddSeatsBanner(
                    responsibleName = uiState.addingSeatsResponsibleName,
                    selectedCount = uiState.selectedSeatNumbers.size,
                    saving = uiState.savingAddedSeats,
                    saveFailed = uiState.addSeatsFailed,
                    onContinue = { onEvent(TourDetailEvent.ReviewAddedSeats) },
                    onCancel = { onEvent(TourDetailEvent.DismissAddSeats) },
                )
            } else if (uiState.selectedSeatNumbers.isNotEmpty()) {
                SeatSelectionActions(
                    selectedCount = uiState.selectedSeatNumbers.size,
                    onReserve = { onEvent(TourDetailEvent.ReserveRequested) },
                    onSell = { onEvent(TourDetailEvent.SellRequested) },
                )
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            uiState.loadFailed -> DetailUnavailableContent(
                title = stringResource(R.string.tour_detail_load_error_title),
                message = stringResource(R.string.tour_detail_load_error_message),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            uiState.notFound || detail == null -> DetailUnavailableContent(
                title = stringResource(R.string.tour_detail_not_found_title),
                message = stringResource(R.string.tour_detail_not_found_message),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            else -> TourDetailList(
                detail = detail,
                title = title,
                dateTime = dateTime,
                selectedSeatNumbers = uiState.highlightedSeatNumbers,
                onlyEmptySeatsEnabled = uiState.changingSeatNumber != null || uiState.addingSeats,
                contentPadding = innerPadding,
                onNavigateBack = onNavigateBack,
                onSeatTapped = { number ->
                    onEvent(TourDetailEvent.SeatTapped(number))
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun TourDetailList(
    detail: TourDetail,
    title: String,
    dateTime: String?,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    contentPadding: PaddingValues,
    onNavigateBack: () -> Unit,
    onSeatTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = contentPadding.calculateBottomPadding() + spacing.large,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            item(key = "tour-header") {
                AppHeroHeader(
                    title = title,
                    icon = Icons.Rounded.DirectionsBus,
                    eyebrow = stringResource(R.string.tour_detail_eyebrow),
                    subtitle = dateTime,
                    onNavigateBack = onNavigateBack,
                    navigateBackContentDescription = stringResource(R.string.navigate_back),
                )
            }

            item(key = "tour-body") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                        )
                        .padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.large),
                ) {
                    SeatOccupancyCard(
                        detail = detail,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    BusSeatMap(
                        detail = detail,
                        selectedSeatNumbers = selectedSeatNumbers,
                        onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
                        onSeatTapped = onSeatTapped,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    TourAccountsCard(
                        accounts = detail.accounts,
                        seats = detail.seats,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SeatLegend(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars),
            color = AppTheme.extendedColors.brandSurface,
            content = {},
        )
    }
}

@Composable
private fun BusSeatMap(
    detail: TourDetail,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val rows = buildBusSeatRows(detail.seats)

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Text(
                text = stringResource(R.string.seat_map_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val dimensions = AppTheme.dimensions
                val density = LocalDensity.current
                val textMeasurer = rememberTextMeasurer()
                val rearSeatCount = (rows.last() as BusSeatRow.Rear).seats.size
                val measuredNumberWidth = with(density) {
                    detail.seats.maxOf { seat ->
                        textMeasurer.measure(
                            text = AnnotatedString(seat.number.toString()),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1,
                        ).size.width
                    }.toDp()
                }
                val minimumSeatWidth = maxOf(
                    dimensions.minimumTouchTarget,
                    measuredNumberWidth + spacing.extraSmall * 2,
                )
                val busHorizontalPadding = spacing.small * 2
                val availableRowWidth =
                    (maxWidth - busHorizontalPadding).coerceAtLeast(spacing.none)
                val rearGaps = spacing.extraSmall * (rearSeatCount - 1)
                val availableSeatWidth = (availableRowWidth - rearGaps) / rearSeatCount
                val seatWidth = maxOf(
                    minimumSeatWidth,
                    minOf(availableSeatWidth, dimensions.seatCell),
                )
                val rowWidth = seatWidth * rearSeatCount + rearGaps
                val busWidth = rowWidth + busHorizontalPadding
                val standardPairGaps = spacing.extraSmall * 2
                val aisleWidth = (
                    rowWidth -
                        seatWidth * STANDARD_BUS_ROW_SIZE -
                        standardPairGaps
                    ).coerceAtLeast(spacing.large)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    contentAlignment = if (busWidth <= maxWidth) {
                        Alignment.TopCenter
                    } else {
                        Alignment.TopStart
                    },
                ) {
                    BusInterior(
                        rows = rows,
                        seatWidth = seatWidth,
                        aisleWidth = aisleWidth,
                        selectedSeatNumbers = selectedSeatNumbers,
                        onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
                        onSeatTapped = onSeatTapped,
                        modifier = Modifier.width(busWidth),
                    )
                }
            }

            SeatSelectionHint()
        }
    }
}

@Composable
private fun BusInterior(
    rows: List<BusSeatRow>,
    seatWidth: Dp,
    aisleWidth: Dp,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val standardRows = rows.filterIsInstance<BusSeatRow.Standard>()
    val rearRow = rows.last() as BusSeatRow.Rear

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
        border = BorderStroke(
            width = spacing.extraSmall * 0.75f,
            color = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small, vertical = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            BusCabin()

            if (standardRows.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.matchParentSize()) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(aisleWidth)
                                .fillMaxHeight(),
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            content = {},
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.small),
                    ) {
                        for (row in standardRows) {
                            StandardBusSeatRow(
                                positions = row.positions,
                                seatWidth = seatWidth,
                                aisleWidth = aisleWidth,
                                selectedSeatNumbers = selectedSeatNumbers,
                                onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
                                onSeatTapped = onSeatTapped,
                            )
                        }
                    }
                }
            }

            RearBusSeatRow(
                seats = rearRow.seats,
                seatWidth = seatWidth,
                selectedSeatNumbers = selectedSeatNumbers,
                onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
                onSeatTapped = onSeatTapped,
            )
        }
    }
}

@Composable
private fun BusCabin() {
    val spacing = AppTheme.spacing

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.dimensions.illustrationContainer),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = spacing.extraSmall * 0.5f,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.large, vertical = spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BusSteeringWheel()
            Text(
                text = stringResource(R.string.bus_front_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun BusSteeringWheel(modifier: Modifier = Modifier) {
    val wheelColor = MaterialTheme.colorScheme.primary
    val strokeWidth = AppTheme.spacing.extraSmall * 0.75f

    Canvas(modifier = modifier.size(AppTheme.dimensions.minimumTouchTarget)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val stroke = strokeWidth.toPx()
        val radius = (size.minDimension - stroke) / 2f

        drawCircle(
            color = wheelColor,
            radius = radius,
            center = center,
            style = Stroke(width = stroke),
        )
        drawCircle(
            color = wheelColor,
            radius = radius * 0.2f,
            center = center,
        )
        drawLine(
            color = wheelColor,
            start = center,
            end = Offset(center.x, center.y - radius),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = wheelColor,
            start = center,
            end = Offset(center.x - radius * 0.82f, center.y + radius * 0.58f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = wheelColor,
            start = center,
            end = Offset(center.x + radius * 0.82f, center.y + radius * 0.58f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SeatSelectionHint() {
    val spacing = AppTheme.spacing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.dimensions.iconMedium),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.seat_map_selection_help),
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StandardBusSeatRow(
    positions: List<TourSeat?>,
    seatWidth: Dp,
    aisleWidth: Dp,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
) {
    val spacing = AppTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BusSeatSlot(
            seat = positions[0],
            selectedSeatNumbers = selectedSeatNumbers,
            onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
            onSeatTapped = onSeatTapped,
            modifier = Modifier
                .width(seatWidth)
                .fillMaxHeight(),
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        BusSeatSlot(
            seat = positions[1],
            selectedSeatNumbers = selectedSeatNumbers,
            onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
            onSeatTapped = onSeatTapped,
            modifier = Modifier
                .width(seatWidth)
                .fillMaxHeight(),
        )
        Spacer(modifier = Modifier.width(aisleWidth))
        BusSeatSlot(
            seat = positions[2],
            selectedSeatNumbers = selectedSeatNumbers,
            onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
            onSeatTapped = onSeatTapped,
            modifier = Modifier
                .width(seatWidth)
                .fillMaxHeight(),
        )
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        BusSeatSlot(
            seat = positions[3],
            selectedSeatNumbers = selectedSeatNumbers,
            onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
            onSeatTapped = onSeatTapped,
            modifier = Modifier
                .width(seatWidth)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun RearBusSeatRow(
    seats: List<TourSeat>,
    seatWidth: Dp,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
) {
    val spacing = AppTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for ((index, seat) in seats.withIndex()) {
            if (index > 0) {
                Spacer(modifier = Modifier.width(spacing.extraSmall))
            }
            TourSeatCell(
                seat = seat,
                selectedSeatNumbers = selectedSeatNumbers,
                onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
                onSeatTapped = onSeatTapped,
                modifier = Modifier
                    .width(seatWidth)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun BusSeatSlot(
    seat: TourSeat?,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (seat == null) {
        Spacer(modifier = modifier)
    } else {
        TourSeatCell(
            seat = seat,
            selectedSeatNumbers = selectedSeatNumbers,
            onlyEmptySeatsEnabled = onlyEmptySeatsEnabled,
            onSeatTapped = onSeatTapped,
            modifier = modifier,
        )
    }
}

@Composable
private fun TourSeatCell(
    seat: TourSeat,
    selectedSeatNumbers: Set<Int>,
    onlyEmptySeatsEnabled: Boolean,
    onSeatTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = seat.number in selectedSeatNumbers
    val enabled = !onlyEmptySeatsEnabled || seat.status == TourSeatStatus.EMPTY

    SeatCell(
        number = seat.number.toString(),
        status = seat.status.toDesignSystemStatus(),
        statusLabel = seatStatusLabel(seat.status),
        seatContentDescription = stringResource(
            R.string.seat_accessibility_label,
            seat.number,
        ),
        selected = selected,
        onClick = { onSeatTapped(seat.number) },
        selectable = enabled,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
private fun SeatOccupancyCard(detail: TourDetail, modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors

    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.large),
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            SeatCountMetric(
                count = detail.usedSeatCount,
                label = stringResource(R.string.used_seats_label),
                containerColor = colors.accentPinkContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f),
            )
            SeatCountMetric(
                count = detail.availableSeatCount,
                label = stringResource(R.string.available_seats_label),
                containerColor = colors.accentGreenContainer,
                contentColor = colors.onSuccessContainer,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TourAccountsCard(
    accounts: TourAccountsSummary,
    seats: List<TourSeat>,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)
    val busCovered = accounts.remainingToCoverFreightCents == 0L
    val credit = accounts.collectedCents > accounts.totalSoldCents
    val creditAmountCents = (accounts.collectedCents - accounts.totalSoldCents).coerceAtLeast(0)
    val pendingNetCents = (accounts.totalSoldCents - accounts.collectedCents).coerceAtLeast(0)

    AppCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.large),
        ) {
            Text(
                text = stringResource(R.string.tour_accounts_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
            )

            AccountsStatusBanner(
                busCovered = busCovered,
                remainingToCoverFreightCents = accounts.remainingToCoverFreightCents,
                currentProfitCents = accounts.currentProfitCents,
                currencySymbol = currencySymbol,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                MoneyStatTile(
                    label = stringResource(R.string.total_tickets_label),
                    amountCents = accounts.totalSoldCents,
                    currencySymbol = currencySymbol,
                    icon = Icons.Rounded.ConfirmationNumber,
                    containerColor = AppTheme.extendedColors.accentSkyContainer,
                    modifier = Modifier.weight(1f),
                )
                MoneyStatTile(
                    label = stringResource(R.string.collected_label),
                    amountCents = accounts.collectedCents,
                    currencySymbol = currencySymbol,
                    icon = Icons.Rounded.Payments,
                    containerColor = AppTheme.extendedColors.accentGreenContainer,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                MoneyStatTile(
                    label = if (credit) {
                        stringResource(R.string.credit_balance_label)
                    } else {
                        stringResource(R.string.pending_collection_short_label)
                    },
                    amountCents = if (credit) creditAmountCents else pendingNetCents,
                    currencySymbol = currencySymbol,
                    icon = if (credit) Icons.Rounded.Savings else Icons.Rounded.HourglassEmpty,
                    containerColor = if (credit) {
                        AppTheme.extendedColors.accentGreenContainer
                    } else {
                        AppTheme.extendedColors.accentYellowContainer
                    },
                    modifier = Modifier.weight(1f),
                )
                MoneyStatTile(
                    label = stringResource(R.string.freight_short_label),
                    amountCents = accounts.freightCents,
                    currencySymbol = currencySymbol,
                    icon = Icons.Rounded.DirectionsBus,
                    containerColor = AppTheme.extendedColors.accentPinkContainer,
                    modifier = Modifier.weight(1f),
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = stringResource(R.string.seat_status_legend_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                SeatStateRow(
                    status = TourSeatStatus.EMPTY,
                    label = stringResource(R.string.seat_state_empty),
                    count = seats.count { it.status == TourSeatStatus.EMPTY },
                )
                SeatStateRow(
                    status = TourSeatStatus.RESERVED,
                    label = stringResource(R.string.seat_state_reserved),
                    count = seats.count { it.status == TourSeatStatus.RESERVED },
                )
                SeatStateRow(
                    status = TourSeatStatus.PARTIAL,
                    label = stringResource(R.string.seat_state_partial),
                    count = seats.count { it.status == TourSeatStatus.PARTIAL },
                )
                SeatStateRow(
                    status = TourSeatStatus.PAID,
                    label = stringResource(R.string.seat_state_paid),
                    count = seats.count { it.status == TourSeatStatus.PAID },
                )
            }
        }
    }
}

@Composable
private fun AccountsStatusBanner(
    busCovered: Boolean,
    remainingToCoverFreightCents: Long,
    currentProfitCents: Long,
    currencySymbol: String,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = if (busCovered) colors.successContainer else colors.warningContainer,
        contentColor = if (busCovered) colors.onSuccessContainer else colors.onWarningContainer,
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (busCovered) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimensions.iconLarge),
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = if (busCovered) {
                        stringResource(R.string.tour_bus_covered_message)
                    } else {
                        stringResource(
                            R.string.tour_bus_uncovered_message,
                            formatMoney(remainingToCoverFreightCents, currencySymbol),
                        )
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text(
                text = stringResource(
                    R.string.tour_current_profit_label,
                    formatMoney(currentProfitCents, currencySymbol),
                ),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun MoneyStatTile(
    label: String,
    amountCents: Long,
    currencySymbol: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = spacing.extraSmall * 0.5f,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
        ) {
            AppIconBadge(
                imageVector = icon,
                contentDescription = null,
                containerColor = containerColor,
            )
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = formatMoney(amountCents, currencySymbol),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun SeatStateRow(
    status: TourSeatStatus,
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors
    val swatchColor = when (status) {
        TourSeatStatus.EMPTY -> colors.seatEmpty
        TourSeatStatus.RESERVED -> colors.seatReserved
        TourSeatStatus.PARTIAL -> colors.seatPartial
        TourSeatStatus.PAID -> colors.seatPaid
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.dimensions.minimumTouchTarget),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.iconMedium)
                    .background(color = swatchColor, shape = CircleShape),
            )
            Text(text = label, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SeatCountMetric(
    count: Int,
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Surface(
        modifier = modifier
            .heightIn(min = AppTheme.dimensions.illustrationContainer)
            .semantics(mergeDescendants = true) {},
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SeatLegend(modifier: Modifier = Modifier) {
    val spacing = AppTheme.spacing

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f),
        border = BorderStroke(
            width = spacing.extraSmall * 0.5f,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = stringResource(R.string.seat_status_legend_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                SeatLegendItem(
                    status = TourSeatStatus.EMPTY,
                    modifier = Modifier.weight(1f),
                )
                SeatLegendItem(
                    status = TourSeatStatus.RESERVED,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                SeatLegendItem(
                    status = TourSeatStatus.PARTIAL,
                    modifier = Modifier.weight(1f),
                )
                SeatLegendItem(
                    status = TourSeatStatus.PAID,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                SeatLegendItem(
                    status = TourSeatStatus.EMPTY,
                    selected = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SeatLegendItem(
    status: TourSeatStatus,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val colors = AppTheme.extendedColors
    val containerColor = when (status) {
        TourSeatStatus.EMPTY -> colors.seatEmpty
        TourSeatStatus.RESERVED -> colors.seatReserved
        TourSeatStatus.PARTIAL -> colors.seatPartial
        TourSeatStatus.PAID -> colors.seatPaid
    }

    Surface(
        modifier = modifier.heightIn(min = AppTheme.dimensions.minimumTouchTarget),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = AppTheme.spacing.extraSmall * 0.25f,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SeatLegendSwatch(
                containerColor = containerColor,
                selected = selected,
            )
            Text(
                text = if (selected) {
                    stringResource(R.string.seat_status_selected)
                } else {
                    seatStatusLabel(status)
                },
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun SeatLegendSwatch(
    containerColor: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Box(
        modifier = modifier.size(AppTheme.dimensions.iconLarge),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.extraSmall, vertical = spacing.extraSmall),
            shape = MaterialTheme.shapes.extraSmall,
            color = containerColor,
            border = BorderStroke(
                width = if (selected) spacing.extraSmall * 0.75f else spacing.extraSmall * 0.5f,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            ),
            content = {},
        )
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(spacing.extraSmall * 0.5f)
                .height(AppTheme.dimensions.iconMedium),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.outlineVariant,
            content = {},
        )
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(spacing.extraSmall * 0.5f)
                .height(AppTheme.dimensions.iconMedium),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.outlineVariant,
            content = {},
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.58f)
                .height(spacing.extraSmall * 0.5f),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.outlineVariant,
            content = {},
        )
    }
}

@Composable
private fun SeatSelectionActions(
    selectedCount: Int,
    onReserve: () -> Unit,
    onSell: () -> Unit,
) {
    val spacing = AppTheme.spacing

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = AppTheme.dimensions.floatingElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                    ),
                )
                .padding(horizontal = spacing.large, vertical = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = pluralStringResource(
                    R.plurals.selected_seat_count,
                    selectedCount,
                    selectedCount,
                ),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                AppButton(
                    text = stringResource(R.string.reserve_action),
                    onClick = onReserve,
                    modifier = Modifier.weight(1f),
                )
                AppButton(
                    text = stringResource(R.string.sell_action),
                    onClick = onSell,
                    modifier = Modifier.weight(1f),
                    style = AppButtonStyle.Outlined,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailUnavailableContent(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(AppTheme.spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        AppEmptyState(
            title = title,
            message = message,
            icon = Icons.Rounded.Warning,
        )
    }
}

@Composable
private fun seatStatusLabel(status: TourSeatStatus): String = when (status) {
    TourSeatStatus.EMPTY -> stringResource(R.string.seat_status_empty)
    TourSeatStatus.RESERVED -> stringResource(R.string.seat_status_reserved)
    TourSeatStatus.PARTIAL -> stringResource(R.string.seat_status_partial)
    TourSeatStatus.PAID -> stringResource(R.string.seat_status_paid)
}

private fun TourSeatStatus.toDesignSystemStatus(): SeatStatus = when (this) {
    TourSeatStatus.EMPTY -> SeatStatus.Empty
    TourSeatStatus.RESERVED -> SeatStatus.Reserved
    TourSeatStatus.PARTIAL -> SeatStatus.Partial
    TourSeatStatus.PAID -> SeatStatus.Paid
}

@Composable
private fun ChangeSeatBanner(
    fromSeatNumber: Int,
    onCancel: () -> Unit,
) {
    val spacing = AppTheme.spacing

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shadowElevation = AppTheme.dimensions.floatingElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                    ),
                )
                .padding(horizontal = spacing.large, vertical = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimensions.iconMedium),
                )
                Text(
                    text = stringResource(R.string.change_seat_banner, fromSeatNumber),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            AppButton(
                text = stringResource(R.string.cancel_action),
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Outlined,
            )
        }
    }
}

@Composable
private fun AddSeatsBanner(
    responsibleName: String,
    selectedCount: Int,
    saving: Boolean,
    saveFailed: Boolean,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
) {
    val spacing = AppTheme.spacing

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shadowElevation = AppTheme.dimensions.floatingElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                    ),
                )
                .padding(horizontal = spacing.large, vertical = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = stringResource(R.string.add_seats_mode_message, responsibleName),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = pluralStringResource(
                    R.plurals.selected_seat_count,
                    selectedCount,
                    selectedCount,
                ),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            if (saveFailed) {
                Text(
                    text = stringResource(R.string.add_seats_error),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
            AppButton(
                text = if (saving) {
                    stringResource(R.string.adding_seats_action)
                } else {
                    stringResource(R.string.add_selected_seats_action)
                },
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCount > 0 && !saving,
            )
            AppButton(
                text = stringResource(R.string.cancel_action),
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Outlined,
                enabled = !saving,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSeatsReviewSheet(
    responsibleName: String,
    selectedSeatNumbers: Set<Int>,
    seatFares: Map<Int, com.gestionviajes.feature.trips.domain.model.FareType>,
    trip: Trip,
    saving: Boolean,
    saveFailed: Boolean,
    onFareSelected: (Int, com.gestionviajes.feature.trips.domain.model.FareType) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val currencySymbol = stringResource(R.string.currency_symbol)
    val allFaresSelected = selectedSeatNumbers.all { number -> seatFares[number] != null }

    ModalBottomSheet(
        onDismissRequest = { if (!saving) onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.large, vertical = spacing.small),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Text(
                text = stringResource(R.string.add_seats_fares_title),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
            )
            BookingSummaryField(
                label = stringResource(R.string.booking_responsible_label),
                value = responsibleName,
            )
            Text(
                text = stringResource(R.string.add_seats_fares_help),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            for (seatNumber in selectedSeatNumbers.sorted()) {
                Text(
                    text = "${stringResource(R.string.seat_label)} $seatNumber",
                    style = MaterialTheme.typography.titleLarge,
                )
                for (fareType in trip.activeFareTypes.sortedBy { it.ordinal }) {
                    val selected = seatFares[seatNumber] == fareType
                    AppButton(
                        text = "${fareLabel(fareType)} - ${formatMoney(checkNotNull(trip.fareCents(fareType)), currencySymbol)}",
                        onClick = { onFareSelected(seatNumber, fareType) },
                        modifier = Modifier.fillMaxWidth(),
                        style = if (selected) AppButtonStyle.Tonal else AppButtonStyle.Outlined,
                        enabled = !saving,
                    )
                }
            }
            if (saveFailed) {
                Text(
                    text = stringResource(R.string.add_seats_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            AppButton(
                text = if (saving) {
                    stringResource(R.string.adding_seats_action)
                } else {
                    stringResource(R.string.confirm_add_seats_action)
                },
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = allFaresSelected && !saving,
            )
            AppButton(
                text = stringResource(R.string.cancel_action),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                style = AppButtonStyle.Outlined,
                enabled = !saving,
            )
            Spacer(modifier = Modifier.height(spacing.small))
        }
    }
}

@Composable
private fun ConfirmSeatMoveDialog(
    fromSeatNumber: Int,
    toSeatNumber: Int,
    moving: Boolean,
    moveFailed: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!moving) onDismiss() },
        title = {
            Text(stringResource(R.string.change_seat_confirm_title, fromSeatNumber, toSeatNumber))
        },
        text = {
            if (moveFailed) {
                Text(
                    text = stringResource(R.string.move_seat_error),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !moving) {
                if (moving) {
                    Text(stringResource(R.string.moving_seat_action))
                } else {
                    Text(stringResource(R.string.confirm_action))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !moving) {
                Text(stringResource(R.string.cancel_action))
            }
        },
    )
}
