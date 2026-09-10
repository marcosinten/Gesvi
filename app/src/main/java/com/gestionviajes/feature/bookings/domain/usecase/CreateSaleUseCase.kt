package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject

data class CreateSaleParams(
    val tripId: Long,
    val responsibleName: String,
    val seats: List<BookingSeat>,
    val amountCents: Long,
)

class CreateSaleUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
) : UseCase<CreateSaleParams, Long>() {
    override suspend fun execute(params: CreateSaleParams): Long {
        require(params.responsibleName.trim().isNotEmpty())
        require(params.seats.isNotEmpty())
        require(params.seats.map(BookingSeat::number).distinct().size == params.seats.size)
        require(params.amountCents > 0)
        return bookingRepository.createSale(
            params.tripId,
            params.responsibleName.trim(),
            params.seats,
            params.amountCents,
        )
    }
}
