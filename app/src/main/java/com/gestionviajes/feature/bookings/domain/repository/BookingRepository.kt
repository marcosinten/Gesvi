package com.gestionviajes.feature.bookings.domain.repository

import com.gestionviajes.feature.bookings.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun observeBookingsForTrip(tripId: Long): Flow<List<Booking>>
    suspend fun createBooking(booking: Booking): Long
}
