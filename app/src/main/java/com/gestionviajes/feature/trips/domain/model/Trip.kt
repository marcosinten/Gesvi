package com.gestionviajes.feature.trips.domain.model

/**
 * Modelo de negocio central para un Viaje.
 *
 * No tiene dependencias de Android, Room o bases de datos.
 */
data class Trip(
    val id: Long = 0,
    val origin: String,
    val destination: String,
    val dateMillis: Long,
    val defaultFare: Double
)
