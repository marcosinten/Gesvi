package com.gestionviajes.feature.trips.domain.repository

import com.gestionviajes.feature.trips.domain.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Contrato que define las operaciones posibles sobre los viajes.
 *
 * En el futuro, la implementación podría mezclar datos de Room
 * y un backend (RemoteDataSource) sin cambiar esta interfaz.
 */
interface TripRepository {
    fun observeAllTrips(): Flow<List<Trip>>
    suspend fun createTrip(trip: Trip): Long
}
