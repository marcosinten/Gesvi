package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BusSeatLayoutTest {

    @Test
    fun `lays out forty seats with a partial standard row and five rear seats`() {
        val rows = buildBusSeatRows(emptySeats(40))

        assertEquals(10, rows.size)
        assertEquals(
            listOf(1, 2, 3, 4),
            (rows.first() as BusSeatRow.Standard).positions.map { it?.number },
        )
        assertEquals(
            listOf(33, 34, 35, null),
            (rows[8] as BusSeatRow.Standard).positions.map { it?.number },
        )
        assertEquals(
            listOf(36, 37, 38, 39, 40),
            (rows.last() as BusSeatRow.Rear).seats.map(TourSeat::number),
        )
    }

    @Test
    fun `keeps a complete standard row before the rear row when capacity is forty one`() {
        val rows = buildBusSeatRows(emptySeats(41))

        assertEquals(
            listOf(33, 34, 35, 36),
            (rows[8] as BusSeatRow.Standard).positions.map { it?.number },
        )
        assertEquals(
            listOf(37, 38, 39, 40, 41),
            (rows.last() as BusSeatRow.Rear).seats.map(TourSeat::number),
        )
    }

    @Test
    fun `never invents or duplicates seats for supported positive capacities`() {
        (1..80).forEach { capacity ->
            val rows = buildBusSeatRows(emptySeats(capacity))
            val renderedNumbers = rows.flatMap { row ->
                when (row) {
                    is BusSeatRow.Standard -> row.positions.mapNotNull { it?.number }
                    is BusSeatRow.Rear -> row.seats.map(TourSeat::number)
                }
            }
            val rearRow = rows.last() as BusSeatRow.Rear

            assertEquals((1..capacity).toList(), renderedNumbers)
            assertEquals(minOf(5, capacity), rearRow.seats.size)
            assertTrue(rows.dropLast(1).all { it is BusSeatRow.Standard })
        }
    }

    private fun emptySeats(capacity: Int): List<TourSeat> = (1..capacity).map { number ->
        TourSeat(number = number, status = TourSeatStatus.EMPTY)
    }
}
