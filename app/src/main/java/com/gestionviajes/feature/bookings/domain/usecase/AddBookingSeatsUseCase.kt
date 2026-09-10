package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class AddBookingSeatsParams(
    val tripId: Long,
    val bookingId: Long,
    val seats: List<BookingSeat>,
)

class AddBookingSeatsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
) : UseCase<AddBookingSeatsParams, Unit>() {

    override suspend fun execute(params: AddBookingSeatsParams) {
        require(params.tripId > 0)
        require(params.bookingId > 0)
        require(params.seats.isNotEmpty())
        require(params.seats.map(BookingSeat::number).distinct().size == params.seats.size)

        val trip = checkNotNull(tripRepository.observeTrip(params.tripId).first())
        require(params.seats.all { seat -> seat.number in 1..trip.seatCount })
        require(params.seats.all { seat -> seat.fareType in trip.activeFareTypes })

        bookingRepository.addSeatsToBooking(
            tripId = params.tripId,
            bookingId = params.bookingId,
            seats = params.seats,
        )
    }
}
