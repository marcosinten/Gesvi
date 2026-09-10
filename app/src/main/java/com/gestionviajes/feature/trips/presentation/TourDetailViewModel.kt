package com.gestionviajes.feature.trips.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.gestionviajes.core.common.Constants.FLOW_TIMEOUT_MS
import com.gestionviajes.core.common.Result
import com.gestionviajes.core.navigation.AppDestination
import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.usecase.GetTourDetailUseCase
import com.gestionviajes.feature.bookings.domain.usecase.CreateSaleUseCase
import com.gestionviajes.feature.bookings.domain.usecase.CreateSaleParams
import com.gestionviajes.feature.bookings.domain.usecase.CreateReservationUseCase
import com.gestionviajes.feature.bookings.domain.usecase.CreateReservationParams
import com.gestionviajes.feature.bookings.domain.usecase.RegisterPaymentParams
import com.gestionviajes.feature.bookings.domain.usecase.RegisterPaymentUseCase
import com.gestionviajes.feature.bookings.domain.usecase.ChangeBookingSeatFareParams
import com.gestionviajes.feature.bookings.domain.usecase.ChangeBookingSeatFareUseCase
import com.gestionviajes.feature.bookings.domain.usecase.ReleaseBookingSeatParams
import com.gestionviajes.feature.bookings.domain.usecase.ReleaseBookingSeatUseCase
import com.gestionviajes.feature.bookings.domain.usecase.DeleteBookingParams
import com.gestionviajes.feature.bookings.domain.usecase.DeleteBookingUseCase
import com.gestionviajes.feature.bookings.domain.usecase.MoveBookingSeatParams
import com.gestionviajes.feature.bookings.domain.usecase.MoveBookingSeatUseCase
import com.gestionviajes.feature.bookings.domain.usecase.AddBookingSeatsParams
import com.gestionviajes.feature.bookings.domain.usecase.AddBookingSeatsUseCase
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.presentation.parseMoneyToCents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TourDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTourDetailUseCase: GetTourDetailUseCase,
    private val createSaleUseCase: CreateSaleUseCase,
    private val createReservationUseCase: CreateReservationUseCase,
    private val registerPaymentUseCase: RegisterPaymentUseCase,
    private val changeBookingSeatFareUseCase: ChangeBookingSeatFareUseCase,
    private val releaseBookingSeatUseCase: ReleaseBookingSeatUseCase,
    private val deleteBookingUseCase: DeleteBookingUseCase,
    private val moveBookingSeatUseCase: MoveBookingSeatUseCase,
    private val addBookingSeatsUseCase: AddBookingSeatsUseCase,
) : ViewModel() {

    private val tourId = savedStateHandle.toRoute<AppDestination.TourDetail>().tourId
    private val selectedSeatNumbers = MutableStateFlow<Set<Int>>(emptySet())
    private val selectedBookingId = MutableStateFlow<Long?>(null)
    private val saleState = MutableStateFlow(SaleState())
    private val paymentState = MutableStateFlow(PaymentFormState())
    private val bookingEditorState = MutableStateFlow(BookingEditorState())
    private val changingSeatState = MutableStateFlow(ChangingSeatState())
    private val addingSeatsState = MutableStateFlow(AddingSeatsState())
    private val bookingDeleteState = MutableStateFlow(BookingDeleteState())
    private val formState = combine(
        saleState,
        paymentState,
        bookingEditorState,
        changingSeatState,
        addingSeatsState,
    ) { sale, payment, editor, changingSeat, addingSeats ->
        TourDetailFormState(
            sale = sale,
            payment = payment,
            editor = editor,
            changingSeat = changingSeat,
            addingSeats = addingSeats,
        )
    }.combine(bookingDeleteState) { forms, bookingDelete ->
        forms.copy(bookingDelete = bookingDelete)
    }

    private val _effects = Channel<TourDetailEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<TourDetailUiState> = combine(
        getTourDetailUseCase(tourId),
        selectedSeatNumbers,
        selectedBookingId,
        formState,
    ) { result, selection, bookingId, forms ->
        when (result) {
            Result.Loading -> TourDetailUiState(isLoading = true)
            is Result.Error -> TourDetailUiState(loadFailed = true)
            is Result.Success -> {
                val detail = result.data
                if (detail == null) {
                    TourDetailUiState(notFound = true)
                } else {
                    val availableNumbers = detail.seats
                        .asSequence()
                        .filter { seat -> seat.status == TourSeatStatus.EMPTY }
                        .map { seat -> seat.number }
                        .toSet()
                    TourDetailUiState(
                        detail = detail,
                        selectedSeatNumbers = selection intersect availableNumbers,
                        selectedBooking = detail.bookings.firstOrNull { booking ->
                            booking.id == bookingId
                        },
                        selling = forms.sale.mode == BookingMode.Sale,
                        reserving = forms.sale.mode == BookingMode.Reservation,
                        responsibleName = forms.sale.responsibleName,
                        receivedAmount = forms.sale.receivedAmount,
                        seatFares = forms.sale.seatFares,
                        registeringPayment = forms.payment.show,
                        paymentAmount = forms.payment.amount,
                        paymentAmountHasError = forms.payment.amountHasError,
                        paymentSaveFailed = forms.payment.saveFailed,
                        savingPayment = forms.payment.saving,
                        editingBooking = forms.editor.show,
                        changingFareSeatNumber = forms.editor.changingFareSeatNumber,
                        savingFareChange = forms.editor.savingFareChange,
                        fareChangeFailed = forms.editor.fareChangeFailed,
                        releasingSeatNumber = forms.editor.releasingSeatNumber,
                        releasingSeat = forms.editor.releasingSeat,
                        releaseSeatFailed = forms.editor.releaseSeatFailed,
                        changingSeatNumber = forms.changingSeat.fromSeatNumber,
                        targetSeatNumber = forms.changingSeat.toSeatNumber,
                        movingSeat = forms.changingSeat.moving,
                        moveSeatFailed = forms.changingSeat.failed,
                        addingSeats = forms.addingSeats.bookingId != null,
                        addingSeatsResponsibleName = forms.addingSeats.responsibleName,
                        addedSeatFares = forms.addingSeats.seatFares,
                        reviewingAddedSeats = forms.addingSeats.reviewing,
                        savingAddedSeats = forms.addingSeats.saving,
                        addSeatsFailed = forms.addingSeats.failed,
                        highlightedBookingSeatNumbers = detail.bookings
                            .firstOrNull { booking ->
                                booking.id == forms.addingSeats.highlightedBookingId
                            }
                            ?.seats
                            ?.mapTo(mutableSetOf()) { seat -> seat.number }
                            .orEmpty(),
                        showDeleteBookingDialog = forms.bookingDelete.bookingId != null,
                        deletingBooking = forms.bookingDelete.deleting,
                        deleteBookingFailed = forms.bookingDelete.failed,
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
        initialValue = TourDetailUiState(isLoading = true),
    )

    fun onEvent(event: TourDetailEvent) {
        when (event) {
            is TourDetailEvent.SeatTapped -> onSeatTapped(event.number)
            TourDetailEvent.ReserveRequested -> openBooking(BookingMode.Reservation)
            TourDetailEvent.SellRequested -> openBooking(BookingMode.Sale)
            is TourDetailEvent.ResponsibleChanged -> saleState.update { it.copy(responsibleName = event.value) }
            is TourDetailEvent.ReceivedChanged -> saleState.update { it.copy(receivedAmount = event.value) }
            is TourDetailEvent.FareChanged -> saleState.update { it.copy(seatFares = it.seatFares + (event.seatNumber to event.fareType)) }
            TourDetailEvent.DismissSale -> saleState.value = SaleState()
            TourDetailEvent.ConfirmSale -> confirmSale()
            TourDetailEvent.ConfirmReservation -> confirmReservation()
            TourDetailEvent.DismissSelectedBooking -> dismissSelectedBooking()
    
            TourDetailEvent.RegisterPaymentRequested -> openPaymentForm()
            is TourDetailEvent.PaymentAmountChanged -> paymentState.update {
                it.copy(
                    amount = event.value,
                    amountHasError = false,
                    saveFailed = false,
                )
            }
            TourDetailEvent.ConfirmPayment -> confirmPayment()
            TourDetailEvent.DismissPayment -> paymentState.value = PaymentFormState()
            TourDetailEvent.EditBookingRequested,
            TourDetailEvent.ManageSelectedBooking -> {
                if (uiState.value.selectedBooking != null) {
                    bookingEditorState.value = BookingEditorState(show = true)
                }
            }
            TourDetailEvent.DismissBookingEditor -> dismissBookingEditor()
            is TourDetailEvent.ChangeFareRequested -> openFareChange(event.seatNumber)
            is TourDetailEvent.FareChangeSelected -> changeFare(event.fareType)
            TourDetailEvent.DismissFareChange -> bookingEditorState.update {
                if (it.savingFareChange) it else it.copy(
                    changingFareSeatNumber = null,
                    fareChangeFailed = false,
                )
            }
            is TourDetailEvent.ReleaseSeatRequested -> requestSeatRelease(event.seatNumber)
            TourDetailEvent.ConfirmSeatRelease -> confirmSeatRelease()
            TourDetailEvent.DismissSeatRelease -> bookingEditorState.update {
                if (it.releasingSeat) it else it.copy(
                    releasingSeatNumber = null,
                    releaseSeatFailed = false,
                )
            }
            is TourDetailEvent.ChangeSeatRequested -> requestSeatChange(event.seatNumber)
            is TourDetailEvent.TargetSeatSelected -> onTargetSeatSelected(event.seatNumber)
            TourDetailEvent.ConfirmSeatMove -> confirmSeatMove()
            TourDetailEvent.DismissSeatMove -> dismissSeatMove()
            TourDetailEvent.AddSeatsRequested -> openAddSeats()
            TourDetailEvent.ReviewAddedSeats -> reviewAddedSeats()
            is TourDetailEvent.AddedSeatFareChanged -> changeAddedSeatFare(
                event.seatNumber,
                event.fareType,
            )
            TourDetailEvent.ConfirmAddedSeats -> confirmAddedSeats()
            TourDetailEvent.DismissAddedSeatsReview -> addingSeatsState.update {
                if (it.saving) it else it.copy(reviewing = false, failed = false)
            }
            TourDetailEvent.DismissAddSeats -> dismissAddSeats()
            TourDetailEvent.DeleteBookingRequested -> requestBookingDeletion()
            TourDetailEvent.ConfirmDeleteBooking -> confirmBookingDeletion()
            TourDetailEvent.DismissDeleteBooking -> dismissBookingDeletion()
        }
    }

    private fun openBooking(mode: BookingMode) {
        val trip = uiState.value.detail?.trip ?: return
        val selection = uiState.value.selectedSeatNumbers
        val default = trip.automaticallySelectedFareType
        selectedBookingId.value = null
        paymentState.value = PaymentFormState()
        bookingEditorState.value = BookingEditorState()
        bookingDeleteState.value = BookingDeleteState()
        saleState.value = SaleState(mode = mode, seatFares = default?.let { fare -> selection.associateWith { fare } }.orEmpty())
    }

    private fun confirmReservation() {
        val state = saleState.value
        val detail = uiState.value.detail ?: return
        val seats = uiState.value.selectedSeatNumbers.mapNotNull { number -> state.seatFares[number]?.let { BookingSeat(number, it) } }
        if (seats.size != uiState.value.selectedSeatNumbers.size) return
        viewModelScope.launch {
            runCatching { createReservationUseCase(CreateReservationParams(detail.trip.id, state.responsibleName, seats)) }
                .onSuccess { selectedSeatNumbers.value = emptySet(); saleState.value = SaleState() }
        }
    }

    private fun confirmSale() {
        val state = saleState.value
        val detail = uiState.value.detail ?: return
        val amount = parseMoneyToCents(state.receivedAmount) ?: return
        val seats = uiState.value.selectedSeatNumbers.mapNotNull { number -> state.seatFares[number]?.let { BookingSeat(number, it) } }
        if (seats.size != uiState.value.selectedSeatNumbers.size) return
        viewModelScope.launch {
            runCatching { createSaleUseCase(CreateSaleParams(detail.trip.id, state.responsibleName, seats, amount)) }
                .onSuccess { selectedSeatNumbers.value = emptySet(); saleState.value = SaleState() }
        }
    }

    private fun onSeatTapped(number: Int) {
        val seat = uiState.value.detail?.seats?.firstOrNull { it.number == number } ?: return
        // Si estamos en modo "cambiar asiento", solo reaccionamos a asientos vacíos
        val changingFrom = changingSeatState.value.fromSeatNumber
        if (changingFrom != null) {
            if (seat.status == TourSeatStatus.EMPTY) {
                onTargetSeatSelected(number)
            }
            return
        }
        if (addingSeatsState.value.bookingId != null) {
            if (seat.status == TourSeatStatus.EMPTY && !addingSeatsState.value.saving) {
                val wasSelected = number in selectedSeatNumbers.value
                selectedSeatNumbers.update { selected -> toggleSeatSelection(selected, seat) }
                addingSeatsState.update { state ->
                    state.copy(
                        seatFares = if (wasSelected) {
                            state.seatFares - number
                        } else {
                            uiState.value.detail?.trip?.automaticallySelectedFareType?.let { fare ->
                                state.seatFares + (number to fare)
                            } ?: state.seatFares
                        },
                        failed = false,
                    )
                }
            }
            return
        }
        addingSeatsState.value = AddingSeatsState()
        paymentState.value = PaymentFormState()
        bookingEditorState.value = BookingEditorState()
        bookingDeleteState.value = BookingDeleteState()
        if (seat.status == TourSeatStatus.EMPTY) {
            selectedBookingId.value = null
            selectedSeatNumbers.update { selected ->
                toggleSeatSelection(selected, seat)
            }
        } else {
            selectedSeatNumbers.value = emptySet()
            selectedBookingId.value = seat.bookingId
        }
    }

    private fun openPaymentForm() {
        if (uiState.value.selectedBooking == null) return
        bookingEditorState.value = BookingEditorState()
        paymentState.value = PaymentFormState(show = true)
    }

    private fun confirmPayment() {
        val bookingId = uiState.value.selectedBooking?.id ?: return
        val amountCents = parseMoneyToCents(paymentState.value.amount)
        if (amountCents == null || amountCents <= 0) {
            paymentState.update { it.copy(amountHasError = true, saveFailed = false) }
            return
        }

        paymentState.update { it.copy(saving = true, amountHasError = false, saveFailed = false) }
        viewModelScope.launch {
            runCatching {
                registerPaymentUseCase(
                    RegisterPaymentParams(
                        bookingId = bookingId,
                        amountCents = amountCents,
                    ),
                )
            }.onSuccess {
                paymentState.value = PaymentFormState()
            }.onFailure {
                paymentState.update { it.copy(saving = false, saveFailed = true) }
            }
        }
    }

    private fun dismissSelectedBooking() {
        paymentState.value = PaymentFormState()
        bookingEditorState.value = BookingEditorState()
        bookingDeleteState.value = BookingDeleteState()
        selectedBookingId.value = null
    }

    private fun dismissBookingEditor() {
        if (!bookingEditorState.value.savingFareChange && !bookingEditorState.value.releasingSeat) {
            bookingEditorState.value = BookingEditorState()
        }
    }

    private fun openFareChange(seatNumber: Int) {
        if (bookingEditorState.value.releasingSeatNumber != null) return
        val booking = uiState.value.selectedBooking ?: return
        if (booking.seats.none { seat -> seat.number == seatNumber }) return
        bookingEditorState.update {
            it.copy(
                changingFareSeatNumber = seatNumber,
                fareChangeFailed = false,
            )
        }
    }

    private fun changeFare(fareType: FareType) {
        val detail = uiState.value.detail ?: return
        val booking = uiState.value.selectedBooking ?: return
        val seatNumber = bookingEditorState.value.changingFareSeatNumber ?: return
        val currentFare = booking.seats.firstOrNull { seat -> seat.number == seatNumber }?.fareType
            ?: return
        if (fareType !in detail.trip.activeFareTypes) return
        if (fareType == currentFare) {
            bookingEditorState.update {
                it.copy(changingFareSeatNumber = null, fareChangeFailed = false)
            }
            return
        }

        bookingEditorState.update { it.copy(savingFareChange = true, fareChangeFailed = false) }
        viewModelScope.launch {
            runCatching {
                changeBookingSeatFareUseCase(
                    ChangeBookingSeatFareParams(
                        tripId = detail.trip.id,
                        bookingId = booking.id,
                        seatNumber = seatNumber,
                        fareType = fareType,
                    ),
                )
            }.onSuccess {
                bookingEditorState.update {
                    it.copy(
                        changingFareSeatNumber = null,
                        savingFareChange = false,
                        fareChangeFailed = false,
                    )
                }
            }.onFailure {
                bookingEditorState.update {
                    it.copy(savingFareChange = false, fareChangeFailed = true)
                }
            }
        }
    }

    private fun requestSeatRelease(seatNumber: Int) {
        if (bookingEditorState.value.savingFareChange) return
        val booking = uiState.value.selectedBooking ?: return
        if (booking.seats.none { seat -> seat.number == seatNumber }) return
        bookingEditorState.update {
            it.copy(
                changingFareSeatNumber = null,
                fareChangeFailed = false,
                releasingSeatNumber = seatNumber,
                releaseSeatFailed = false,
            )
        }
    }

    private fun confirmSeatRelease() {
        val detail = uiState.value.detail ?: return
        val booking = uiState.value.selectedBooking ?: return
        val seatNumber = bookingEditorState.value.releasingSeatNumber ?: return
        if (booking.seats.none { seat -> seat.number == seatNumber }) return

        bookingEditorState.update { it.copy(releasingSeat = true, releaseSeatFailed = false) }
        viewModelScope.launch {
            runCatching {
                releaseBookingSeatUseCase(
                    ReleaseBookingSeatParams(
                        tripId = detail.trip.id,
                        bookingId = booking.id,
                        seatNumber = seatNumber,
                    ),
                )
            }.onSuccess {
                bookingEditorState.update {
                    it.copy(
                        releasingSeatNumber = null,
                        releasingSeat = false,
                        releaseSeatFailed = false,
                    )
                }
            }.onFailure {
                bookingEditorState.update {
                    it.copy(releasingSeat = false, releaseSeatFailed = true)
                }
            }
        }
    }

    private fun requestSeatChange(seatNumber: Int) {
        if (bookingEditorState.value.releasingSeat || bookingEditorState.value.savingFareChange) return
        val booking = uiState.value.selectedBooking ?: return
        if (booking.seats.none { seat -> seat.number == seatNumber }) return
        // Cerrar el editor y activar modo cambio de asiento
        bookingEditorState.value = BookingEditorState()
        changingSeatState.value = ChangingSeatState(
            fromSeatNumber = seatNumber,
            bookingId = booking.id,
        )
        selectedBookingId.value = null
    }

    private fun onTargetSeatSelected(seatNumber: Int) {
        if (changingSeatState.value.moving) return
        changingSeatState.update { it.copy(toSeatNumber = seatNumber, failed = false) }
    }

    private fun confirmSeatMove() {
        val detail = uiState.value.detail ?: return
        val state = changingSeatState.value
        val fromSeat = state.fromSeatNumber ?: return
        val toSeat = state.toSeatNumber ?: return
        val bookingId = state.bookingId ?: return
        val booking = uiState.value.detail?.bookings?.firstOrNull { it.id == bookingId } ?: return
        val fareType = booking.seats.firstOrNull { it.number == fromSeat }?.fareType ?: return

        changingSeatState.update { it.copy(moving = true, failed = false) }
        viewModelScope.launch {
            runCatching {
                moveBookingSeatUseCase(
                    MoveBookingSeatParams(
                        tripId = detail.trip.id,
                        bookingId = bookingId,
                        fromSeatNumber = fromSeat,
                        toSeatNumber = toSeat,
                        fareType = fareType,
                    ),
                )
            }.onSuccess {
                changingSeatState.value = ChangingSeatState()
            }.onFailure {
                changingSeatState.update { it.copy(moving = false, failed = true) }
            }
        }
    }

    private fun dismissSeatMove() {
        if (changingSeatState.value.moving) return
        changingSeatState.value = ChangingSeatState()
    }

    private fun openAddSeats() {
        val booking = uiState.value.selectedBooking ?: return
        selectedSeatNumbers.value = emptySet()
        selectedBookingId.value = null
        saleState.value = SaleState()
        paymentState.value = PaymentFormState()
        bookingEditorState.value = BookingEditorState()
        changingSeatState.value = ChangingSeatState()
        addingSeatsState.value = AddingSeatsState(
            bookingId = booking.id,
            responsibleName = booking.responsibleName,
        )
    }

    private fun reviewAddedSeats() {
        if (selectedSeatNumbers.value.isEmpty() || addingSeatsState.value.saving) return
        if (uiState.value.detail?.trip?.automaticallySelectedFareType != null) {
            confirmAddedSeats()
        } else {
            addingSeatsState.update { it.copy(reviewing = true, failed = false) }
        }
    }

    private fun changeAddedSeatFare(seatNumber: Int, fareType: FareType) {
        val trip = uiState.value.detail?.trip ?: return
        if (seatNumber !in uiState.value.selectedSeatNumbers || fareType !in trip.activeFareTypes) return
        addingSeatsState.update {
            it.copy(seatFares = it.seatFares + (seatNumber to fareType), failed = false)
        }
    }

    private fun confirmAddedSeats() {
        val detail = uiState.value.detail ?: return
        val state = addingSeatsState.value
        val bookingId = state.bookingId ?: return
        val selectedNumbers = uiState.value.selectedSeatNumbers
        if (selectedNumbers.isEmpty()) return
        val seats = selectedNumbers.mapNotNull { number ->
            state.seatFares[number]?.let { fareType -> BookingSeat(number, fareType) }
        }
        if (seats.size != selectedNumbers.size) return

        addingSeatsState.update { it.copy(saving = true, failed = false) }
        viewModelScope.launch {
            runCatching {
                addBookingSeatsUseCase(
                    AddBookingSeatsParams(
                        tripId = detail.trip.id,
                        bookingId = bookingId,
                        seats = seats,
                    ),
                )
            }.onSuccess {
                selectedSeatNumbers.value = emptySet()
                addingSeatsState.value = AddingSeatsState(highlightedBookingId = bookingId)
            }.onFailure {
                addingSeatsState.update { it.copy(saving = false, failed = true) }
            }
        }
    }

    private fun dismissAddSeats() {
        if (addingSeatsState.value.saving) return
        selectedSeatNumbers.value = emptySet()
        addingSeatsState.value = AddingSeatsState()
    }

    private fun requestBookingDeletion() {
        if (bookingDeleteState.value.deleting) return
        val booking = uiState.value.selectedBooking ?: return
        if (booking.status != TourSeatStatus.RESERVED) return
        bookingEditorState.value = BookingEditorState()
        paymentState.value = PaymentFormState()
        bookingDeleteState.value = BookingDeleteState(bookingId = booking.id)
    }

    private fun confirmBookingDeletion() {
        val detail = uiState.value.detail ?: return
        val bookingId = bookingDeleteState.value.bookingId ?: return
        if (bookingDeleteState.value.deleting) return
        bookingDeleteState.update { it.copy(deleting = true, failed = false) }
        viewModelScope.launch {
            runCatching {
                deleteBookingUseCase(
                    DeleteBookingParams(
                        tripId = detail.trip.id,
                        bookingId = bookingId,
                    ),
                )
            }.onSuccess {
                bookingDeleteState.value = BookingDeleteState()
                selectedBookingId.value = null
                saleState.value = SaleState()
            }.onFailure {
                bookingDeleteState.update { it.copy(deleting = false, failed = true) }
            }
        }
    }

    private fun dismissBookingDeletion() {
        if (bookingDeleteState.value.deleting) return
        bookingDeleteState.value = BookingDeleteState()
    }
}

private enum class BookingMode { Sale, Reservation }
private data class SaleState(val mode: BookingMode? = null, val responsibleName: String = "", val receivedAmount: String = "", val seatFares: Map<Int, FareType> = emptyMap())
private data class PaymentFormState(
    val show: Boolean = false,
    val amount: String = "",
    val amountHasError: Boolean = false,
    val saveFailed: Boolean = false,
    val saving: Boolean = false,
)
private data class BookingEditorState(
    val show: Boolean = false,
    val changingFareSeatNumber: Int? = null,
    val savingFareChange: Boolean = false,
    val fareChangeFailed: Boolean = false,
    val releasingSeatNumber: Int? = null,
    val releasingSeat: Boolean = false,
    val releaseSeatFailed: Boolean = false,
)
private data class ChangingSeatState(
    val fromSeatNumber: Int? = null,
    val bookingId: Long? = null,
    val toSeatNumber: Int? = null,
    val moving: Boolean = false,
    val failed: Boolean = false,
)
private data class AddingSeatsState(
    val bookingId: Long? = null,
    val responsibleName: String = "",
    val seatFares: Map<Int, FareType> = emptyMap(),
    val reviewing: Boolean = false,
    val saving: Boolean = false,
    val failed: Boolean = false,
    val highlightedBookingId: Long? = null,
)
private data class BookingDeleteState(
    val bookingId: Long? = null,
    val deleting: Boolean = false,
    val failed: Boolean = false,
)
private data class TourDetailFormState(
    val sale: SaleState,
    val payment: PaymentFormState,
    val editor: BookingEditorState,
    val changingSeat: ChangingSeatState,
    val addingSeats: AddingSeatsState,
    val bookingDelete: BookingDeleteState = BookingDeleteState(),
)

internal fun toggleSeatSelection(
    selectedSeatNumbers: Set<Int>,
    seat: TourSeat,
): Set<Int> {
    if (seat.status != TourSeatStatus.EMPTY) return selectedSeatNumbers
    return if (seat.number in selectedSeatNumbers) {
        selectedSeatNumbers - seat.number
    } else {
        selectedSeatNumbers + seat.number
    }
}
