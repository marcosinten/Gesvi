package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.FlowUseCase
import com.gestionviajes.feature.bookings.domain.model.Booking
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookingsForTripUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) : FlowUseCase<Long, List<Booking>>() {

    /** @param params tripId */
    override fun execute(params: Long): Flow<List<Booking>> =
        bookingRepository.observeBookingsForTrip(params)
}
