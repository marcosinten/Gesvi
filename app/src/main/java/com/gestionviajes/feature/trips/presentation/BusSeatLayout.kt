package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.TourSeat

internal sealed interface BusSeatRow {
    data class Standard(val positions: List<TourSeat?>) : BusSeatRow {
        init {
            require(positions.size == STANDARD_BUS_ROW_SIZE)
        }
    }

    data class Rear(val seats: List<TourSeat>) : BusSeatRow {
        init {
            require(seats.size in 1..REAR_BUS_ROW_SIZE)
        }
    }
}

internal fun buildBusSeatRows(seats: List<TourSeat>): List<BusSeatRow> {
    if (seats.isEmpty()) return emptyList()

    val rearSeats = seats.takeLast(REAR_BUS_ROW_SIZE)
    val standardRows = seats
        .dropLast(rearSeats.size)
        .chunked(STANDARD_BUS_ROW_SIZE)
        .map { seatsInRow ->
            BusSeatRow.Standard(
                positions = seatsInRow +
                    List(STANDARD_BUS_ROW_SIZE - seatsInRow.size) { null },
            )
        }

    return standardRows + BusSeatRow.Rear(rearSeats)
}

internal const val STANDARD_BUS_ROW_SIZE = 4
internal const val REAR_BUS_ROW_SIZE = 5
