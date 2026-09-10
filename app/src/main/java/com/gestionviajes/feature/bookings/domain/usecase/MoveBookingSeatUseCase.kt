package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import com.gestionviajes.feature.trips.domain.model.FareType
import javax.inject.Inject

data class MoveBookingSeatParams(
    val tripId: Long,
    val bookingId: Long,
    val fromSeatNumber: Int,
    val toSeatNumber: Int,
    val fareType: FareType,
)

class MoveBookingSeatUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) : UseCase<MoveBookingSeatParams, Unit>() {
    override suspend fun execute(params: MoveBookingSeatParams) {
        require(params.tripId > 0)
        require(params.bookingId > 0)
        require(params.fromSeatNumber > 0)
        require(params.toSeatNumber > 0)
        require(params.fromSeatNumber != params.toSeatNumber)
        bookingRepository.moveBookingSeat(
            tripId = params.tripId,
            bookingId = params.bookingId,
            fromSeatNumber = params.fromSeatNumber,
            toSeatNumber = params.toSeatNumber,
            fareType = params.fareType,
        )
    }
}
