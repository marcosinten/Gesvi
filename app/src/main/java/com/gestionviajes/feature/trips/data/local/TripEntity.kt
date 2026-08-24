package com.gestionviajes.feature.trips.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Representación Room del Tour. Esta clase no sale de Data. */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val destination: String?,
    val supervisor: String?,
    val dateMillis: Long,
    val seatCount: Int,
    val freightCents: Long,
    val roundTripFareCents: Long?,
    val outboundFareCents: Long?,
    val returnFareCents: Long?,
    val isRoundTripFareEnabled: Boolean,
    val isOutboundFareEnabled: Boolean,
    val isReturnFareEnabled: Boolean,
    /** Instante absoluto (fecha + hora) de salida. Nulo en Tours sin definir. */
    val horaSalidaMillis: Long? = null,
    /** Instante absoluto (fecha + hora) de venida. Puede caer en otro día. */
    val horaVenidaMillis: Long? = null,
)
