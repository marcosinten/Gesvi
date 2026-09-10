package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.Trip
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

internal data class CreateTourValidationResult(
    val trip: Trip?,
    val invalidFields: Set<CreateTourField>,
)

private val MoneyPattern = Regex("^\\d+(?:[.,]\\d{1,2})?$")

internal fun validateCreateTourForm(
    state: CreateTourUiState,
    zoneId: ZoneId = ZoneId.systemDefault(),
): CreateTourValidationResult {
    val invalidFields = mutableSetOf<CreateTourField>()
    val destination = state.destination.trim()
    val supervisor = state.supervisor.trim()
    val seatCount = state.seatCount.trim().toIntOrNull()
    val freightCents = parseMoneyToCents(state.freight)
    val roundTripFareCents = parseOptionalMoneyToCents(state.roundTripFare)
    val outboundFareCents = parseOptionalMoneyToCents(state.outboundFare)
    val returnFareCents = parseOptionalMoneyToCents(state.returnFare)

    if (destination.isEmpty()) invalidFields += CreateTourField.Destination
    if (supervisor.isEmpty()) invalidFields += CreateTourField.Supervisor
    if (seatCount == null || seatCount <= 0) invalidFields += CreateTourField.SeatCount
    if (freightCents == null || freightCents < 0) invalidFields += CreateTourField.Freight
    if (state.enabledFareCount == 0) invalidFields += CreateTourField.FareConfiguration
    if (state.isRoundTripFareEnabled && (roundTripFareCents == null || roundTripFareCents <= 0)) {
        invalidFields += CreateTourField.RoundTripFare
    }
    if (state.isOutboundFareEnabled && (outboundFareCents == null || outboundFareCents <= 0)) {
        invalidFields += CreateTourField.OutboundFare
    }
    if (state.isReturnFareEnabled && (returnFareCents == null || returnFareCents <= 0)) {
        invalidFields += CreateTourField.ReturnFare
    }

    val date = parseDate(state)
    if (date == null) invalidFields += CreateTourField.Date

    val time = parseTime(state)
    if (time == null) invalidFields += CreateTourField.Time

    val venidaTime = parseVenidaTime(state)
    if (venidaTime == null) invalidFields += CreateTourField.ReturnTime

    if (invalidFields.isNotEmpty()) {
        return CreateTourValidationResult(trip = null, invalidFields = invalidFields)
    }

    val dateMillis = LocalDateTime.of(checkNotNull(date), checkNotNull(time))
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
    val horaSalidaMillis = dateMillis

    // horaVenidaMillis es un instante absoluto (fecha + hora), no solo una hora
    // del reloj (ver Trip.kt). Decisión de fecha: mismo día del Tour por defecto.
    // TODO revisar UX: si la venida cruza medianoche habría que permitir elegir
    // día posterior en vez de asumirlo automáticamente.
    val horaVenidaMillis = if (!state.isVenidaDefined) {
        null
    } else {
        LocalDateTime.of(checkNotNull(date), checkNotNull(venidaTime))
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }
    if (horaVenidaMillis != null && horaVenidaMillis <= horaSalidaMillis) {
        invalidFields += CreateTourField.ReturnTime
        return CreateTourValidationResult(trip = null, invalidFields = invalidFields)
    }

    return CreateTourValidationResult(
        trip = Trip(
            destination = destination,
            supervisor = supervisor,
            dateMillis = dateMillis,
            horaSalidaMillis = horaSalidaMillis,
            horaVenidaMillis = horaVenidaMillis,
            seatCount = checkNotNull(seatCount),
            freightCents = checkNotNull(freightCents),
            roundTripFareCents = roundTripFareCents?.takeIf { it > 0 },
            outboundFareCents = outboundFareCents?.takeIf { it > 0 },
            returnFareCents = returnFareCents?.takeIf { it > 0 },
            isRoundTripFareEnabled = state.isRoundTripFareEnabled,
            isOutboundFareEnabled = state.isOutboundFareEnabled,
            isReturnFareEnabled = state.isReturnFareEnabled,
        ),
        invalidFields = emptySet(),
    )
}

internal fun parseMoneyToCents(value: String): Long? {
    val normalized = value.trim()
    if (!MoneyPattern.matches(normalized)) return null

    val parts = normalized.replace(',', '.').split('.', limit = 2)
    val wholeUnits = parts[0].toLongOrNull() ?: return null
    val fractionalUnits = when (val fraction = parts.getOrNull(1)) {
        null -> 0L
        else -> fraction.padEnd(2, '0').toLongOrNull() ?: return null
    }

    return try {
        Math.addExact(Math.multiplyExact(wholeUnits, 100L), fractionalUnits)
    } catch (_: ArithmeticException) {
        null
    }
}

private fun parseOptionalMoneyToCents(value: String): Long? =
    value.trim().takeIf(String::isNotEmpty)?.let(::parseMoneyToCents)

private fun parseDate(state: CreateTourUiState): LocalDate? {
    val day = state.day.trim().toIntOrNull() ?: return null
    if (state.year !in state.minimumYear..state.maximumYear) return null

    return try {
        LocalDate.of(state.year, state.month, day)
    } catch (_: DateTimeException) {
        null
    }
}

private fun parseTime(state: CreateTourUiState): LocalTime? = try {
    LocalTime.of(state.hour, state.minute)
} catch (_: DateTimeException) {
    null
}

private fun parseVenidaTime(state: CreateTourUiState): LocalTime? {
    if (!state.isVenidaDefined) return LocalTime.MIDNIGHT
    return try {
        LocalTime.of(state.venidaHour, state.venidaMinute)
    } catch (_: DateTimeException) {
        null
    }
}
