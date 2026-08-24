package com.gestionviajes.feature.seats.domain

import com.gestionviajes.core.domain.FlowUseCase
import com.gestionviajes.feature.bookings.domain.model.Seat
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Genera el estado visual del mapa de asientos.
 *
 * REGLA ARQUITECTÓNICA: Los asientos no tienen tabla propia.
 * Se calculan a partir de las reservas ([BookingRepository]).
 */
class GetSeatMapUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) : FlowUseCase<Long, List<Seat>>() {

    /** @param params tripId */
    override fun execute(params: Long): Flow<List<Seat>> =
        bookingRepository.observeBookingsForTrip(params).map { bookings ->
            // TODO: Lógica para mapear Bookings a la lista completa de Seats
            emptyList()
        }
}
