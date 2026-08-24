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
    fun observeTrip(tripId: Long): Flow<Trip?>
    suspend fun createTrip(trip: Trip): Long
    suspend fun deleteTrip(tripId: Long) {
        error("La eliminación de Tours no está disponible en este repositorio.")
    }
    /** Las implementaciones de producción deben persistir el Tour actualizado. */
    suspend fun updateTrip(trip: Trip) {
        error("La actualización de Tours no está disponible en este repositorio.")
    }
}
