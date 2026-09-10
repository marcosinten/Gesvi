package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.UseCase
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) : UseCase<Long, Unit>() {

    override suspend fun execute(params: Long) {
        require(params > 0)
        tripRepository.deleteTrip(params)
    }
}
