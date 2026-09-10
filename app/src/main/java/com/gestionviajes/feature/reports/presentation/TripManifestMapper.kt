package com.gestionviajes.feature.reports.presentation

import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.TourDetail

data class ManifestSeatRow(
    val seatNumber: Int,
    val fareType: FareType?,
    val name: String,
    val abonoCents: Long?,
)

/**
 * Proyecta los asientos y pasajeros para el Informe de Viaje.
 *
 * Reglas de negocio solicitadas:
 * - Ida y vuelta: fila única (no se duplica).
 * - Solo ida: se duplica; la primera fila muestra Ida con el nombre y abono del
 *   pasajero, y la segunda fila muestra Venida sin nombre ni abono.
 * - Solo venida: se duplica al contrario; la primera fila muestra Venida con el
 *   nombre y abono del pasajero, y la segunda fila muestra Ida sin nombre ni abono.
 * - Si un mismo asiento fue tomado por pasajeros distintos en ida y venida, ambas
 *   filas se listan con sus respectivos pasajeros y abonos.
 * - Asientos sin reservas no se muestran en el informe.
 */
fun buildTripManifestRows(detail: TourDetail): List<ManifestSeatRow> {
    val rows = mutableListOf<ManifestSeatRow>()

    for (seatNumber in 1..detail.trip.seatCount) {
        val seatBookings = detail.bookings.mapNotNull { booking ->
            val bookingSeat = booking.seats.firstOrNull { it.number == seatNumber }
            if (bookingSeat != null) booking to bookingSeat else null
        }

        if (seatBookings.isEmpty()) continue

        val roundTrip = seatBookings.firstOrNull { it.second.fareType == FareType.ROUND_TRIP }
        val outbound = seatBookings.firstOrNull { it.second.fareType == FareType.OUTBOUND }
        val returnLeg = seatBookings.firstOrNull { it.second.fareType == FareType.RETURN }

        when {
            roundTrip != null -> {
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.ROUND_TRIP,
                        name = roundTrip.first.responsibleName,
                        abonoCents = roundTrip.first.receivedCents,
                    )
                )
            }
            outbound != null && returnLeg != null -> {
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.OUTBOUND,
                        name = outbound.first.responsibleName,
                        abonoCents = outbound.first.receivedCents,
                    )
                )
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.RETURN,
                        name = returnLeg.first.responsibleName,
                        abonoCents = returnLeg.first.receivedCents,
                    )
                )
            }
            outbound != null -> {
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.OUTBOUND,
                        name = outbound.first.responsibleName,
                        abonoCents = outbound.first.receivedCents,
                    )
                )
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.RETURN,
                        name = "-",
                        abonoCents = null,
                    )
                )
            }
            returnLeg != null -> {
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.RETURN,
                        name = returnLeg.first.responsibleName,
                        abonoCents = returnLeg.first.receivedCents,
                    )
                )
                rows.add(
                    ManifestSeatRow(
                        seatNumber = seatNumber,
                        fareType = FareType.OUTBOUND,
                        name = "-",
                        abonoCents = null,
                    )
                )
            }
        }
    }

    return rows
}
