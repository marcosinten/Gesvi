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

    override suspend fun execute(params: Trip): Long =
        tripRepository.createTrip(params)
}
