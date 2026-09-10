package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject

data class RegisterPaymentParams(
    val bookingId: Long,
    val amountCents: Long,
)

class RegisterPaymentUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) : UseCase<RegisterPaymentParams, Long>() {
    override suspend fun execute(params: RegisterPaymentParams): Long {
        require(params.bookingId > 0)
        require(params.amountCents > 0)
        return bookingRepository.registerPayment(
            bookingId = params.bookingId,
            amountCents = params.amountCents,
        )
    }
}
