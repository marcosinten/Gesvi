package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject

class UpdateTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) : UseCase<Trip, Unit>() {

    override suspend fun execute(params: Trip) {
        require(params.id > 0)
        tripRepository.updateTrip(params)
    }
}
