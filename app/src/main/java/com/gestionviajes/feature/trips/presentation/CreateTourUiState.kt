package com.gestionviajes.feature.trips.presentation

import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import com.gestionviajes.feature.trips.domain.model.Trip

enum class CreateTourField {
    Destination,
    Supervisor,
    Date,
    Time,
    ReturnTime,
    SeatCount,
    Freight,
    FareConfiguration,
    RoundTripFare,
    OutboundFare,
    ReturnFare,
}

/** Estado inmutable del formulario Crear Tour. */
data class CreateTourUiState(
    val destination: String = "",
    val supervisor: String = "",
    val day: String,
    val month: Int,
    val year: Int,
    val minimumYear: Int,
    val maximumYear: Int,
    val hour: Int,
    val minute: Int,
    val venidaHour: Int,
    val venidaMinute: Int,
    val isVenidaDefined: Boolean = false,
    val seatCount: String = "",
    val freight: String = "",
    val isRoundTripFareEnabled: Boolean = true,
    val roundTripFare: String = "",
    val isOutboundFareEnabled: Boolean = false,
    val outboundFare: String = "",
    val isReturnFareEnabled: Boolean = false,
    val returnFare: String = "",
    val invalidFields: Set<CreateTourField> = emptySet(),
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,
) {
    val enabledFareCount: Int
        get() = listOf(
            isRoundTripFareEnabled,
            isOutboundFareEnabled,
            isReturnFareEnabled,
        ).count { it }
    companion object {
        fun initial(now: LocalDateTime = LocalDateTime.now()) = CreateTourUiState(
            day = now.dayOfMonth.toString(),
            month = now.monthValue,
            year = now.year,
            minimumYear = now.year,
            maximumYear = now.year + 10,
            hour = now.hour,
            minute = now.minute,
            venidaHour = now.hour,
            venidaMinute = now.minute,
            isVenidaDefined = false,
        )

        fun fromTrip(trip: Trip, zoneId: ZoneId = ZoneId.systemDefault()): CreateTourUiState {
            val dateTime = Instant.ofEpochMilli(trip.dateMillis).atZone(zoneId).toLocalDateTime()
            // horaSalidaMillis es un instante absoluto; si falta (Tours antiguos),
            // se usa dateMillis como respaldo para no romper el formulario.
            val salidaDateTime = trip.horaSalidaMillis
                ?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDateTime() }
                ?: dateTime
            val venidaDateTime = trip.horaVenidaMillis
                ?.let { Instant.ofEpochMilli(it).atZone(zoneId).toLocalDateTime() }
            return CreateTourUiState(
                destination = trip.destination.orEmpty(),
                supervisor = trip.supervisor.orEmpty(),
                day = dateTime.dayOfMonth.toString(),
                month = dateTime.monthValue,
                year = dateTime.year,
                minimumYear = dateTime.year,
                maximumYear = dateTime.year + 10,
                hour = salidaDateTime.hour,
                minute = salidaDateTime.minute,
                venidaHour = venidaDateTime?.hour ?: salidaDateTime.hour,
                venidaMinute = venidaDateTime?.minute ?: salidaDateTime.minute,
                isVenidaDefined = venidaDateTime != null,
                seatCount = trip.seatCount.toString(),
                freight = formatCents(trip.freightCents),
                isRoundTripFareEnabled = trip.isRoundTripFareEnabled,
                roundTripFare = trip.roundTripFareCents?.let(::formatCents).orEmpty(),
                isOutboundFareEnabled = trip.isOutboundFareEnabled,
                outboundFare = trip.outboundFareCents?.let(::formatCents).orEmpty(),
                isReturnFareEnabled = trip.isReturnFareEnabled,
                returnFare = trip.returnFareCents?.let(::formatCents).orEmpty(),
            )
        }
    }
}

private fun formatCents(cents: Long): String = "%d.%02d".format(cents / 100, cents % 100)
