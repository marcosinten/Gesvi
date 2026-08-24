package com.gestionviajes.feature.trips.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY dateMillis DESC")
    fun observeAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun observeTrip(tripId: Long): Flow<TripEntity?>

    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity): Int

    @Query(
        """
        DELETE FROM booking_seat_cross_ref
        WHERE bookingId IN (SELECT id FROM bookings WHERE tripId = :tripId)
        """,
    )
    suspend fun deleteBookingSeatsForTrip(tripId: Long)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long): Int

    @Transaction
    suspend fun deleteTripWithRelatedData(tripId: Long) {
        deleteBookingSeatsForTrip(tripId)
        check(deleteTripById(tripId) == 1) { "Tour no encontrado" }
    }
}
