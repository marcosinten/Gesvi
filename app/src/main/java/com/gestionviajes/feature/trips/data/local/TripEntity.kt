package com.gestionviajes.feature.trips.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representación de un viaje en la base de datos (Room).
 * Esta clase NUNCA debe salir de la capa Data.
 */
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val origin: String,
    val destination: String,
    val dateMillis: Long,
    val defaultFare: Double
)
