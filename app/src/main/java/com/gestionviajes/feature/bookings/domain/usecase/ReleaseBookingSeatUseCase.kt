package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject

data class ReleaseBookingSeatParams(
    val tripId: Long,
    val bookingId: Long,
    val seatNumber: Int,
)

class ReleaseBookingSeatUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) : UseCase<ReleaseBookingSeatParams, Unit>() {
    override suspend fun execute(params: ReleaseBookingSeatParams) {
        require(params.tripId > 0)
        require(params.bookingId > 0)
        require(params.seatNumber > 0)
        bookingRepository.releaseBookingSeat(
            tripId = params.tripId,
            bookingId = params.bookingId,
            seatNumber = params.seatNumber,
        )
    }
}
