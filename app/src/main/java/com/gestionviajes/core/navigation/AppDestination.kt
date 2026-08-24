package com.gestionviajes.core.navigation

import kotlinx.serialization.Serializable

/**
 * Rutas de la aplicación fuertemente tipadas usando kotlinx.serialization.
 *
 * Requerimiento de Navigation Compose 2.8.0+.
 */
sealed interface AppDestination {

    @Serializable
    data object TripList : AppDestination

    @Serializable
    data class SeatMap(val tripId: Long) : AppDestination

    @Serializable
    data class PassengerList(val tripId: Long) : AppDestination

    @Serializable
    data class Accounts(val tripId: Long) : AppDestination

    @Serializable
    data object Settings : AppDestination
}
