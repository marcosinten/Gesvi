package com.gestionviajes.feature.bookings.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gestionviajes.feature.trips.data.local.TripEntity

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val passengerName: String,
)

/**
 * Tabla de relación muchos a muchos, aunque en la práctica
 * un asiento en un viaje específico solo pertenece a una reserva.
 */
@Entity(
    tableName = "booking_seat_cross_ref",
    primaryKeys = ["bookingId", "seatNumber"]
)
data class BookingSeatCrossRef(
    val bookingId: Long,
    val seatNumber: Int,
    val fareType: String,
)
