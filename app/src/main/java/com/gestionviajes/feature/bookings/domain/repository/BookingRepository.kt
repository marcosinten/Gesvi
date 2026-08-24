package com.gestionviajes.feature.bookings.domain.repository

import com.gestionviajes.feature.bookings.domain.model.Booking
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.trips.domain.model.FareType
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun observeBookingsForTrip(tripId: Long): Flow<List<Booking>>
    suspend fun createSale(tripId: Long, responsibleName: String, seats: List<BookingSeat>, amountCents: Long): Long
    suspend fun createReservation(tripId: Long, responsibleName: String, seats: List<BookingSeat>): Long
    suspend fun addSeatsToBooking(tripId: Long, bookingId: Long, seats: List<BookingSeat>) {
        error("Agregar asientos a una reserva no está disponible en este repositorio.")
    }
    suspend fun registerPayment(bookingId: Long, amountCents: Long): Long
    suspend fun changeBookingSeatFare(
        tripId: Long,
        bookingId: Long,
        seatNumber: Int,
        fareType: FareType,
    )
    suspend fun releaseBookingSeat(tripId: Long, bookingId: Long, seatNumber: Int)
    suspend fun deleteBooking(tripId: Long, bookingId: Long)
    suspend fun moveBookingSeat(
        tripId: Long,
        bookingId: Long,
        fromSeatNumber: Int,
        toSeatNumber: Int,
        fareType: FareType,
    )
}
