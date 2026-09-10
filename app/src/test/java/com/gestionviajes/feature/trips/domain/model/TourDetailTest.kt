package com.gestionviajes.feature.trips.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TourDetailTest {

    @Test
    fun `overpaid booking does not hide another booking pending balance`() {
        val overpaid = booking(id = 1L, totalCents = 8_000L, receivedCents = 10_000L)
        val unpaid = booking(id = 2L, totalCents = 5_000L, receivedCents = 0L)
        val accounts = accounts(bookings = listOf(overpaid, unpaid))

        assertEquals(5_000L, accounts.pendingCollectionCents)
    }

    @Test
    fun `exactly paid bookings leave zero pending balance`() {
        val accounts = accounts(
            bookings = listOf(
                booking(id = 1L, totalCents = 8_000L, receivedCents = 8_000L),
                booking(id = 2L, totalCents = 4_500L, receivedCents = 4_500L),
            ),
        )

        assertEquals(0L, accounts.pendingCollectionCents)
    }

    @Test
    fun `no bookings leave zero pending balance`() {
        assertEquals(0L, accounts(bookings = emptyList()).pendingCollectionCents)
    }

    private fun booking(id: Long, totalCents: Long, receivedCents: Long) = TourBookingSummary(
        id = id,
        responsibleName = "Responsable $id",
        seats = emptyList(),
        totalCents = totalCents,
        receivedCents = receivedCents,
        status = TourSeatStatus.PAID,
    )

    private fun accounts(bookings: List<TourBookingSummary>) = TourAccountsSummary(
        freightCents = 0L,
        totalSoldCents = bookings.sumOf(TourBookingSummary::totalCents),
        collectedCents = bookings.sumOf(TourBookingSummary::receivedCents),
        bookings = bookings,
    )
}
