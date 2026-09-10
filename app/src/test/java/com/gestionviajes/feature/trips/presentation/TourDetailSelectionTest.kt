package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TourDetailSelectionTest {

    @Test
    fun `selects multiple empty seats and toggles each independently`() {
        val seatOne = TourSeat(number = 1, status = TourSeatStatus.EMPTY)
        val seatTwo = TourSeat(number = 2, status = TourSeatStatus.EMPTY)

        val firstSelection = toggleSeatSelection(emptySet(), seatOne)
        val secondSelection = toggleSeatSelection(firstSelection, seatTwo)
        val finalSelection = toggleSeatSelection(secondSelection, seatOne)

        assertEquals(setOf(1), firstSelection)
        assertEquals(setOf(1, 2), secondSelection)
        assertEquals(setOf(2), finalSelection)
    }

    @Test
    fun `does not select an occupied seat`() {
        val occupiedSeat = TourSeat(
            number = 3,
            status = TourSeatStatus.RESERVED,
            bookingId = 20L,
        )

        assertEquals(setOf(1), toggleSeatSelection(setOf(1), occupiedSeat))
    }
}
