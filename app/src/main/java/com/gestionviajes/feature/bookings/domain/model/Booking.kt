package com.gestionviajes.feature.bookings.domain.model

import com.gestionviajes.core.designsystem.token.SeatStatus

/** Proyección temporal del mapa; la autoridad sigue siendo Booking. */
data class Seat(val number: String, val status: SeatStatus, val passengerName: String? = null)

data class Booking(
    val id: Long = 0,
    val tripId: Long,
    val passengerName: String,
    val seats: List<BookingSeat>,
    val payments: List<PaymentRecord>,
)

data class BookingSeat(val number: Int, val fareType: com.gestionviajes.feature.trips.domain.model.FareType)

data class PaymentRecord(val amountCents: Long, val dateMillis: Long)
