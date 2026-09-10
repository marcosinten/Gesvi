package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class ChangeBookingSeatFareParams(
    val tripId: Long,
    val bookingId: Long,
    val seatNumber: Int,
    val fareType: FareType,
)

class ChangeBookingSeatFareUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
) : UseCase<ChangeBookingSeatFareParams, Unit>() {
    override suspend fun execute(params: ChangeBookingSeatFareParams) {
        require(params.bookingId > 0)
        require(params.seatNumber > 0)
        val trip = checkNotNull(tripRepository.observeTrip(params.tripId).first())
        require(params.fareType in trip.activeFareTypes)
        bookingRepository.changeBookingSeatFare(
            tripId = params.tripId,
            bookingId = params.bookingId,
            seatNumber = params.seatNumber,
            fareType = params.fareType,
        )
    }
}
