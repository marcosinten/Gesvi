package com.gestionviajes.feature.trips.data.mapper

import com.gestionviajes.feature.trips.data.local.TripEntity
import com.gestionviajes.feature.trips.domain.model.Trip

/**
 * Transforma entre modelo de datos (Entity) y modelo de dominio.
 *
 * Mantiene la capa de Dominio completamente independiente de Room.
 */
fun TripEntity.toDomain() = Trip(
    id = id,
    origin = origin,
    destination = destination,
    dateMillis = dateMillis,
    defaultFare = defaultFare
)

fun Trip.toEntity() = TripEntity(
    id = id,
    origin = origin,
    destination = destination,
    dateMillis = dateMillis,
    defaultFare = defaultFare
)
