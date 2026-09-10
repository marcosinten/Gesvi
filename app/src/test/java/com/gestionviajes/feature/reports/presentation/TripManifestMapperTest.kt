package com.gestionviajes.feature.reports.presentation

import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.TourBookingSeat
import com.gestionviajes.feature.trips.domain.model.TourBookingSummary
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.model.Trip
import org.junit.Assert.assertEquals
import org.junit.Test

class TripManifestMapperTest {

    private fun createTrip(seatCount: Int = 10) = Trip(
        id = 1L,
        dateMillis = 1000L,
        horaSalidaMillis = 1000L,
        horaVenidaMillis = 2000L,
        seatCount = seatCount,
        freightCents = 10000L,
        destination = "Playas",
        supervisor = "Carlos",
        roundTripFareCents = 2000L,
        outboundFareCents = 1200L,
        returnFareCents = 1200L,
        isRoundTripFareEnabled = true,
        isOutboundFareEnabled = true,
        isReturnFareEnabled = true,
    )

    private fun createDetail(
        trip: Trip,
        bookings: List<TourBookingSummary>,
    ): TourDetail {
        val seats = (1..trip.seatCount).map { number ->
            TourSeat(number = number, status = TourSeatStatus.EMPTY)
        }
        return TourDetail(
            trip = trip,
            seats = seats,
            bookings = bookings,
        )
    }

    @Test
    fun roundTripSeat_isNotDuplicated() {
        val trip = createTrip()
        val booking = TourBookingSummary(
            id = 10L,
            responsibleName = "Sra. Lopez",
            seats = listOf(TourBookingSeat(number = 1, fareType = FareType.ROUND_TRIP, fareCents = 2000L)),
            totalCents = 2000L,
            receivedCents = 1500L,
            status = TourSeatStatus.PARTIAL,
        )
        val detail = createDetail(trip, listOf(booking))

        val rows = buildTripManifestRows(detail)

        assertEquals(1, rows.size)
        assertEquals(1, rows[0].seatNumber)
        assertEquals(FareType.ROUND_TRIP, rows[0].fareType)
        assertEquals("Sra. Lopez", rows[0].name)
        assertEquals(1500L, rows[0].abonoCents)
    }

    @Test
    fun outboundSeat_isDuplicatedWithEmptyReturnRow() {
        val trip = createTrip()
        val booking = TourBookingSummary(
            id = 10L,
            responsibleName = "Juan Perez",
            seats = listOf(TourBookingSeat(number = 2, fareType = FareType.OUTBOUND, fareCents = 1200L)),
            totalCents = 1200L,
            receivedCents = 1200L,
            status = TourSeatStatus.PAID,
        )
        val detail = createDetail(trip, listOf(booking))

        val rows = buildTripManifestRows(detail)

        assertEquals(2, rows.size)
        // Row 1: Ida with passenger
        assertEquals(2, rows[0].seatNumber)
        assertEquals(FareType.OUTBOUND, rows[0].fareType)
        assertEquals("Juan Perez", rows[0].name)
        assertEquals(1200L, rows[0].abonoCents)
        // Row 2: Venida empty
        assertEquals(2, rows[1].seatNumber)
        assertEquals(FareType.RETURN, rows[1].fareType)
        assertEquals("-", rows[1].name)
        assertEquals(null, rows[1].abonoCents)
    }

    @Test
    fun returnSeat_isDuplicatedOppositeWithEmptyOutboundRow() {
        val trip = createTrip()
        val booking = TourBookingSummary(
            id = 20L,
            responsibleName = "Maria Gomez",
            seats = listOf(TourBookingSeat(number = 3, fareType = FareType.RETURN, fareCents = 1200L)),
            totalCents = 1200L,
            receivedCents = 800L,
            status = TourSeatStatus.PARTIAL,
        )
        val detail = createDetail(trip, listOf(booking))

        val rows = buildTripManifestRows(detail)

        assertEquals(2, rows.size)
        // Row 1: Venida with passenger
        assertEquals(3, rows[0].seatNumber)
        assertEquals(FareType.RETURN, rows[0].fareType)
        assertEquals("Maria Gomez", rows[0].name)
        assertEquals(800L, rows[0].abonoCents)
        // Row 2: Ida empty
        assertEquals(3, rows[1].seatNumber)
        assertEquals(FareType.OUTBOUND, rows[1].fareType)
        assertEquals("-", rows[1].name)
        assertEquals(null, rows[1].abonoCents)
    }

    @Test
    fun differentPassengersInOutboundAndReturn_showsBothPassengers() {
        val trip = createTrip()
        val outboundBooking = TourBookingSummary(
            id = 10L,
            responsibleName = "Juan Perez",
            seats = listOf(TourBookingSeat(number = 4, fareType = FareType.OUTBOUND, fareCents = 1200L)),
            totalCents = 1200L,
            receivedCents = 1200L,
            status = TourSeatStatus.PAID,
        )
        val returnBooking = TourBookingSummary(
            id = 20L,
            responsibleName = "Maria Gomez",
            seats = listOf(TourBookingSeat(number = 4, fareType = FareType.RETURN, fareCents = 1200L)),
            totalCents = 1200L,
            receivedCents = 1200L,
            status = TourSeatStatus.PAID,
        )
        val detail = createDetail(trip, listOf(outboundBooking, returnBooking))

        val rows = buildTripManifestRows(detail)

        assertEquals(2, rows.size)
        assertEquals(4, rows[0].seatNumber)
        assertEquals(FareType.OUTBOUND, rows[0].fareType)
        assertEquals("Juan Perez", rows[0].name)
        assertEquals(1200L, rows[0].abonoCents)

        assertEquals(4, rows[1].seatNumber)
        assertEquals(FareType.RETURN, rows[1].fareType)
        assertEquals("Maria Gomez", rows[1].name)
        assertEquals(1200L, rows[1].abonoCents)
    }

    @Test
    fun unbookedSeats_areOmitted() {
        val trip = createTrip(seatCount = 5)
        val detail = createDetail(trip, emptyList())

        val rows = buildTripManifestRows(detail)

        assertEquals(0, rows.size)
    }
}
