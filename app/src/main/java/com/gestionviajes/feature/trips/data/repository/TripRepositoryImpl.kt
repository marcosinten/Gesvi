package com.gestionviajes.feature.trips.data.repository

import com.gestionviajes.feature.trips.data.local.TripDao
import com.gestionviajes.feature.trips.data.mapper.toDomain
import com.gestionviajes.feature.trips.data.mapper.toEntity
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación concreta del repositorio.
 *
 * Esta clase conoce sobre Room y los DAOs, pero implementa
 * una interfaz pura del Dominio.
 */
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {

    override fun observeAllTrips(): Flow<List<Trip>> =
        tripDao.observeAllTrips().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeTrip(tripId: Long): Flow<Trip?> =
        tripDao.observeTrip(tripId).map { entity -> entity?.toDomain() }

    override suspend fun createTrip(trip: Trip): Long =
        tripDao.insertTrip(trip.toEntity())

    override suspend fun deleteTrip(tripId: Long) {
        tripDao.deleteTripWithRelatedData(tripId)
    }

    override suspend fun updateTrip(trip: Trip) {
        check(tripDao.updateTrip(trip.toEntity()) == 1) { "Tour no encontrado" }
    }
}
