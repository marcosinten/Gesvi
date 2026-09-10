package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject

data class DeleteBookingParams(
    val tripId: Long,
    val bookingId: Long,
)

class DeleteBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) : UseCase<DeleteBookingParams, Unit>() {
    override suspend fun execute(params: DeleteBookingParams) {
        require(params.tripId > 0)
        require(params.bookingId > 0)
        bookingRepository.deleteBooking(
            tripId = params.tripId,
            bookingId = params.bookingId,
        )
    }
}