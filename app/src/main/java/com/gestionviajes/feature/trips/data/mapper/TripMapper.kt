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
    destination = destination,
    supervisor = supervisor,
    dateMillis = dateMillis,
    seatCount = seatCount,
    freightCents = freightCents,
    roundTripFareCents = roundTripFareCents,
    outboundFareCents = outboundFareCents,
    returnFareCents = returnFareCents,
    isRoundTripFareEnabled = isRoundTripFareEnabled,
    isOutboundFareEnabled = isOutboundFareEnabled,
    isReturnFareEnabled = isReturnFareEnabled,
    horaSalidaMillis = horaSalidaMillis,
    horaVenidaMillis = horaVenidaMillis,
)

fun Trip.toEntity() = TripEntity(
    id = id,
    destination = destination,
    supervisor = supervisor,
    dateMillis = dateMillis,
    seatCount = seatCount,
    freightCents = freightCents,
    roundTripFareCents = roundTripFareCents,
    outboundFareCents = outboundFareCents,
    returnFareCents = returnFareCents,
    isRoundTripFareEnabled = isRoundTripFareEnabled,
    isOutboundFareEnabled = isOutboundFareEnabled,
    isReturnFareEnabled = isReturnFareEnabled,
    horaSalidaMillis = horaSalidaMillis,
    horaVenidaMillis = horaVenidaMillis,
)
