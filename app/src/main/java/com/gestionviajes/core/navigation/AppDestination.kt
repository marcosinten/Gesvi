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
    data object Reports : AppDestination

    @Serializable
    data object TripReport : AppDestination

    @Serializable
    data object TourReport : AppDestination

    @Serializable
    data object Refunds : AppDestination

    @Serializable
    data object History : AppDestination

    @Serializable
    data object CreateTour : AppDestination

    @Serializable
    data class EditTour(val tourId: Long) : AppDestination

    @Serializable
    data class TourDetail(val tourId: Long) : AppDestination

    @Serializable
    data class SeatMap(val tripId: Long) : AppDestination

    @Serializable
    data class PassengerList(val tripId: Long) : AppDestination

    @Serializable
    data class Accounts(val tripId: Long) : AppDestination

    @Serializable
    data object Settings : AppDestination
}
