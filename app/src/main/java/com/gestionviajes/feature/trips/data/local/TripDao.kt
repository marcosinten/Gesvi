package com.gestionviajes.feature.trips.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY dateMillis DESC")
    fun observeAllTrips(): Flow<List<TripEntity>>

    @Insert
    suspend fun insertTrip(trip: TripEntity): Long
}
