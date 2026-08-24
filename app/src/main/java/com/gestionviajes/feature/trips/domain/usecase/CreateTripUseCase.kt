package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject

/**
 * Caso de uso para crear un viaje nuevo.
 */
class CreateTripUseCase @Inject constructor(
    private val tripRepository: TripRepository
) : UseCase<Trip, Long>() {

    override suspend fun execute(params: Trip): Long {
        val destination = params.destination?.trim().orEmpty()
        val supervisor = params.supervisor?.trim().orEmpty()

        require(destination.isNotEmpty()) { "El destino es obligatorio." }
        require(supervisor.isNotEmpty()) { "El supervisor es obligatorio." }

        return tripRepository.createTrip(
            params.copy(
                destination = destination,
                supervisor = supervisor,
            ),
        )
    }
}
