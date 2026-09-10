package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourBookingSummary
import com.gestionviajes.feature.trips.domain.model.FareType

data class TourDetailUiState(
    val isLoading: Boolean = false,
    val detail: TourDetail? = null,
    val selectedSeatNumbers: Set<Int> = emptySet(),
    val selectedBooking: TourBookingSummary? = null,
    val loadFailed: Boolean = false,
    val notFound: Boolean = false,
    val selling: Boolean = false,
    val reserving: Boolean = false,
    val responsibleName: String = "",
    val receivedAmount: String = "",
    val seatFares: Map<Int, FareType> = emptyMap(),
    val registeringPayment: Boolean = false,
    val paymentAmount: String = "",
    val paymentAmountHasError: Boolean = false,
    val paymentSaveFailed: Boolean = false,
    val savingPayment: Boolean = false,
    val editingBooking: Boolean = false,
    val changingFareSeatNumber: Int? = null,
    val savingFareChange: Boolean = false,
    val fareChangeFailed: Boolean = false,
    val releasingSeatNumber: Int? = null,
    val releasingSeat: Boolean = false,
    val releaseSeatFailed: Boolean = false,
    val changingSeatNumber: Int? = null,
    val targetSeatNumber: Int? = null,
    val movingSeat: Boolean = false,
    val moveSeatFailed: Boolean = false,
    val addingSeats: Boolean = false,
    val addingSeatsResponsibleName: String = "",
    val addedSeatFares: Map<Int, FareType> = emptyMap(),
    val reviewingAddedSeats: Boolean = false,
    val savingAddedSeats: Boolean = false,
    val addSeatsFailed: Boolean = false,
    val highlightedBookingSeatNumbers: Set<Int> = emptySet(),
    val showDeleteBookingDialog: Boolean = false,
    val deletingBooking: Boolean = false,
    val deleteBookingFailed: Boolean = false,
) {
    val highlightedSeatNumbers: Set<Int>
        get() = when {
            targetSeatNumber != null -> setOf(targetSeatNumber)
            changingSeatNumber != null -> emptySet()
            addingSeats -> selectedSeatNumbers
            highlightedBookingSeatNumbers.isNotEmpty() -> highlightedBookingSeatNumbers
            selectedBooking != null -> selectedBooking.seats.mapTo(mutableSetOf()) { seat -> seat.number }
            else -> selectedSeatNumbers
        }
}

sealed interface TourDetailEvent {
    data class SeatTapped(val number: Int) : TourDetailEvent
    data object ReserveRequested : TourDetailEvent
    data object SellRequested : TourDetailEvent
    data class ResponsibleChanged(val value: String) : TourDetailEvent
    data class ReceivedChanged(val value: String) : TourDetailEvent
    data class FareChanged(val seatNumber: Int, val fareType: FareType) : TourDetailEvent
    data object ConfirmSale : TourDetailEvent
    data object DismissSale : TourDetailEvent
    data object ConfirmReservation : TourDetailEvent
    data object DismissSelectedBooking : TourDetailEvent
    data object ManageSelectedBooking : TourDetailEvent
    data object RegisterPaymentRequested : TourDetailEvent
    data class PaymentAmountChanged(val value: String) : TourDetailEvent
    data object ConfirmPayment : TourDetailEvent
    data object DismissPayment : TourDetailEvent
    data object EditBookingRequested : TourDetailEvent
    data object DismissBookingEditor : TourDetailEvent
    data class ChangeFareRequested(val seatNumber: Int) : TourDetailEvent
    data class FareChangeSelected(val fareType: FareType) : TourDetailEvent
    data object DismissFareChange : TourDetailEvent
    data class ReleaseSeatRequested(val seatNumber: Int) : TourDetailEvent
    data object ConfirmSeatRelease : TourDetailEvent
    data object DismissSeatRelease : TourDetailEvent
    data class ChangeSeatRequested(val seatNumber: Int) : TourDetailEvent
    data class TargetSeatSelected(val seatNumber: Int) : TourDetailEvent
    data object ConfirmSeatMove : TourDetailEvent
    data object DismissSeatMove : TourDetailEvent
    data object AddSeatsRequested : TourDetailEvent
    data object ReviewAddedSeats : TourDetailEvent
    data class AddedSeatFareChanged(val seatNumber: Int, val fareType: FareType) : TourDetailEvent
    data object ConfirmAddedSeats : TourDetailEvent
    data object DismissAddedSeatsReview : TourDetailEvent
    data object DismissAddSeats : TourDetailEvent
    data object DeleteBookingRequested : TourDetailEvent
    data object ConfirmDeleteBooking : TourDetailEvent
    data object DismissDeleteBooking : TourDetailEvent
}

enum class UnavailableTourAction {
    Reserve,
    Sell,
    BookingManagement,
}

sealed interface TourDetailEffect {
    data class ShowUnavailable(val action: UnavailableTourAction) : TourDetailEffect
}
