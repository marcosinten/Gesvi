package com.gestionviajes.feature.bookings.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings WHERE tripId = :tripId")
    fun observeBookingsForTrip(tripId: Long): kotlinx.coroutines.flow.Flow<List<BookingEntity>>

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    @Insert
    suspend fun insertBookingSeats(crossRefs: List<BookingSeatCrossRef>)
}
