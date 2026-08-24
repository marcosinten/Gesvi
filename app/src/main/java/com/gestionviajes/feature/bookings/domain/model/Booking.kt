package com.gestionviajes.feature.bookings.domain.model

import com.gestionviajes.core.designsystem.token.SeatStatus

/**
 * Representa un asiento físico en el vehículo.
 */
data class Seat(
    val number: String,
    val status: SeatStatus,
    val passengerName: String? = null
)

/**
 * Representa una reserva realizada por un pasajero, que puede
 * incluir múltiples asientos.
 */
data class Booking(
    val id: Long = 0,
    val tripId: Long,
    val passengerName: String,
    val totalFare: Double,
    val seats: List<Seat> = emptyList()
)
