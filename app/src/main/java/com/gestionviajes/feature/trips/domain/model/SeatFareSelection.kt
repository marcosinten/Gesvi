package com.gestionviajes.feature.trips.domain.model

/**
 * Elección transitoria para los formularios de reservar y vender.
 * Con una sola tarifa activa no se solicita una elección adicional.
 */
data class SeatFareSelection(
    val seatNumber: Int,
    val fareType: FareType,
)

fun defaultSeatFareSelections(
    seatNumbers: Set<Int>,
    trip: Trip,
): List<SeatFareSelection>? {
    val fareType = trip.automaticallySelectedFareType ?: return null
    return seatNumbers.sorted().map { seatNumber ->
        SeatFareSelection(seatNumber = seatNumber, fareType = fareType)
    }
}
