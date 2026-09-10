package com.gestionviajes.feature.trips.presentation

sealed interface CreateTourEvent {
    data class DestinationChanged(val value: String) : CreateTourEvent
    data class SupervisorChanged(val value: String) : CreateTourEvent
    data class DayChanged(val value: String) : CreateTourEvent
    data class MonthChanged(val value: Int) : CreateTourEvent
    data class YearChanged(val value: Int) : CreateTourEvent
    data class TimeChanged(val hour: Int, val minute: Int) : CreateTourEvent
    data class VenidaTimeChanged(val hour: Int, val minute: Int) : CreateTourEvent
    data class VenidaDefinedChanged(val defined: Boolean) : CreateTourEvent
    data class SeatCountChanged(val value: String) : CreateTourEvent
    data class FreightChanged(val value: String) : CreateTourEvent
    data object EnableRoundTripFare : CreateTourEvent
    data object RemoveRoundTripFare : CreateTourEvent
    data class RoundTripFareChanged(val value: String) : CreateTourEvent
    data object EnableOutboundFare : CreateTourEvent
    data object RemoveOutboundFare : CreateTourEvent
    data class OutboundFareChanged(val value: String) : CreateTourEvent
    data object EnableReturnFare : CreateTourEvent
    data object RemoveReturnFare : CreateTourEvent
    data class ReturnFareChanged(val value: String) : CreateTourEvent
    data object Submit : CreateTourEvent
}

sealed interface CreateTourEffect {
    data class Created(val tourId: Long) : CreateTourEffect
}
