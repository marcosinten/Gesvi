package com.gestionviajes.feature.bookings.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject

data class CreateReservationParams(val tripId: Long, val responsibleName: String, val seats: List<BookingSeat>)

class CreateReservationUseCase @Inject constructor(private val bookingRepository: BookingRepository) : UseCase<CreateReservationParams, Long>() {
    override suspend fun execute(params: CreateReservationParams): Long {
        require(params.responsibleName.trim().isNotEmpty())
        require(params.seats.isNotEmpty())
        require(params.seats.map(BookingSeat::number).distinct().size == params.seats.size)
        return bookingRepository.createReservation(params.tripId, params.responsibleName.trim(), params.seats)
    }
}
