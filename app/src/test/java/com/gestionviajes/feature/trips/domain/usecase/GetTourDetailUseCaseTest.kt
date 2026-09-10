package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.feature.bookings.domain.model.Booking
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetTourDetailUseCaseTest {

    @Test
    fun `generates every configured seat in stable numeric order`() = runTest {
        val repository = FakeTripRepository(tour(seatCount = 40))
        val detail = checkNotNull(GetTourDetailUseCase(repository, FakeBookingRepository()).execute(7L).first())

        assertEquals((1..40).toList(), detail.seats.map { seat -> seat.number })
        assertTrue(detail.seats.all { seat -> seat.status == TourSeatStatus.EMPTY })
        assertEquals(0, detail.usedSeatCount)
        assertEquals(40, detail.availableSeatCount)
    }

    @Test
    fun `emits null when the tour does not exist`() = runTest {
        val repository = FakeTripRepository(null)

        assertNull(GetTourDetailUseCase(repository, FakeBookingRepository()).execute(99L).first())
    }

    @Test
    fun `counts every occupied status as used`() {
        val detail = TourDetail(
            trip = tour(seatCount = 4),
            seats = listOf(
                TourSeat(number = 1, status = TourSeatStatus.EMPTY),
                TourSeat(number = 2, status = TourSeatStatus.RESERVED, bookingId = 10L),
                TourSeat(number = 3, status = TourSeatStatus.PARTIAL, bookingId = 11L),
                TourSeat(number = 4, status = TourSeatStatus.PAID, bookingId = 12L),
            ),
        )

        assertEquals(3, detail.usedSeatCount)
        assertEquals(1, detail.availableSeatCount)
    }

    private fun tour(seatCount: Int) = Trip(
        id = 7L,
        destination = "Cali",
        supervisor = "Marta",
        dateMillis = 1_800_000_000_000L,
        seatCount = seatCount,
        freightCents = 0L,
        roundTripFareCents = 7_000L,
    )

    private class FakeTripRepository(initialTrip: Trip?) : TripRepository {
        private val trip = MutableStateFlow(initialTrip)

        override fun observeAllTrips(): Flow<List<Trip>> = emptyFlow()

        override fun observeTrip(tripId: Long): Flow<Trip?> = trip

        override suspend fun createTrip(trip: Trip): Long = error("Not used")
    }

    private class FakeBookingRepository : BookingRepository {
        override fun observeBookingsForTrip(tripId: Long): Flow<List<Booking>> = flowOf(emptyList())

        override suspend fun createSale(
            tripId: Long,
            responsibleName: String,
            seats: List<BookingSeat>,
            amountCents: Long,
        ): Long = error("Not used")

        override suspend fun createReservation(
            tripId: Long,
            responsibleName: String,
            seats: List<BookingSeat>,
        ): Long = error("Not used")

        override suspend fun registerPayment(bookingId: Long, amountCents: Long): Long = error("Not used")

        override suspend fun changeBookingSeatFare(
            tripId: Long,
            bookingId: Long,
            seatNumber: Int,
            fareType: FareType,
        ): Unit = error("Not used")

        override suspend fun releaseBookingSeat(tripId: Long, bookingId: Long, seatNumber: Int): Unit =
            error("Not used")

        override suspend fun moveBookingSeat(
            tripId: Long,
            bookingId: Long,
            fromSeatNumber: Int,
            toSeatNumber: Int,
            fareType: FareType,
        ): Unit = error("Not used")

        override suspend fun deleteBooking(tripId: Long, bookingId: Long): Unit = error("Not used")
    }
}
