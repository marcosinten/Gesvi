package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class CreateTripUseCaseTest {

    @Test
    fun `trims required text before persisting the tour`() = runTest {
        val repository = RecordingTripRepository()
        val useCase = CreateTripUseCase(repository)

        val id = useCase(validTrip().copy(destination = "  Cali ", supervisor = " Marta  "))

        assertEquals(7L, id)
        assertEquals("Cali", repository.createdTrip?.destination)
        assertEquals("Marta", repository.createdTrip?.supervisor)
    }

    @Test
    fun `rejects a blank destination before reaching the repository`() = runTest {
        val repository = RecordingTripRepository()
        val useCase = CreateTripUseCase(repository)

        try {
            useCase(validTrip().copy(destination = "  "))
            fail("Expected a blank destination to be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected domain validation.
        }

        assertNull(repository.createdTrip)
    }

    private fun validTrip() = Trip(
        destination = "Cali",
        supervisor = "Marta",
        dateMillis = 1_800_000_000_000L,
        seatCount = 40,
        freightCents = 0L,
        roundTripFareCents = 7_000L,
    )

    private class RecordingTripRepository : TripRepository {
        var createdTrip: Trip? = null

        override fun observeAllTrips(): Flow<List<Trip>> = emptyFlow()

        override fun observeTrip(tripId: Long): Flow<Trip?> = flowOf(null)

        override suspend fun createTrip(trip: Trip): Long {
            createdTrip = trip
            return 7L
        }
    }
}
