package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.FlowUseCase
import com.gestionviajes.core.domain.NoParams
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los viajes.
 *
 * Hereda de [FlowUseCase] por lo que su salida automáticamente
 * es envuelta en un Result<List<Trip>> que maneja excepciones.
 */
class GetTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository
) : FlowUseCase<NoParams, List<Trip>>() {

    override fun execute(params: NoParams): Flow<List<Trip>> =
        tripRepository.observeAllTrips()
}
