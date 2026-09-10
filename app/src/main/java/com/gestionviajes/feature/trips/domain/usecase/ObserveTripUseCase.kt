package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.FlowUseCase
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTripUseCase @Inject constructor(
    private val tripRepository: TripRepository,
) : FlowUseCase<Long, Trip?>() {
    override fun execute(params: Long): Flow<Trip?> = tripRepository.observeTrip(params)
}
