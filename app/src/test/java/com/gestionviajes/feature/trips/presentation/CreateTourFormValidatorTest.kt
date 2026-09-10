package com.gestionviajes.feature.trips.presentation

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateTourFormValidatorTest {

    @Test
    fun `converts decimal text to cents without floating point`() {
        assertEquals(3_550L, parseMoneyToCents("35.50"))
        assertEquals(3_550L, parseMoneyToCents("35,5"))
        assertEquals(3_500L, parseMoneyToCents(" 35 "))
        assertEquals(5L, parseMoneyToCents("0.05"))
        assertNull(parseMoneyToCents("-1"))
        assertNull(parseMoneyToCents("1.234"))
        assertNull(parseMoneyToCents("92233720368547758.08"))
    }

    @Test
    fun `builds a valid tour with trimmed text and exact amounts`() {
        val state = validState().copy(
            destination = "  Medellín  ",
            supervisor = "  Ana Ruiz  ",
            day = "29",
            month = 2,
            year = 2028,
            hour = 6,
            minute = 45,
            freight = "1250,50",
            roundTripFare = "80.00",
            isOutboundFareEnabled = true,
            outboundFare = "45.5",
        )

        val result = validateCreateTourForm(state, ZoneOffset.UTC)
        val trip = checkNotNull(result.trip)

        assertTrue(result.invalidFields.isEmpty())
        assertEquals("Medellín", trip.destination)
        assertEquals("Ana Ruiz", trip.supervisor)
        assertEquals(125_050L, trip.freightCents)
        assertEquals(8_000L, trip.roundTripFareCents)
        assertEquals(4_550L, trip.outboundFareCents)
        assertNull(trip.returnFareCents)
        assertEquals(
            LocalDateTime.of(2028, 2, 29, 6, 45)
                .atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli(),
            trip.dateMillis,
        )
    }

    @Test
    fun `marks every invalid required and enabled field`() {
        val state = validState().copy(
            destination = " ",
            supervisor = "",
            day = "30",
            month = 2,
            hour = 24,
            isVenidaDefined = true,
            venidaHour = 24,
            seatCount = "0",
            freight = "-1",
            roundTripFare = "0",
            isOutboundFareEnabled = true,
            outboundFare = "",
            isReturnFareEnabled = true,
            returnFare = "0",
        )

        val result = validateCreateTourForm(state, ZoneOffset.UTC)

        assertNull(result.trip)
        // FareConfiguration queda fuera: con tres tipos habilitados la
        // configuración es válida aunque cada precio individual falle.
        assertEquals(
            CreateTourField.entries.toSet() - CreateTourField.FareConfiguration,
            result.invalidFields,
        )
    }

    @Test
    fun `flags fare configuration when every fare type is disabled`() {
        val result = validateCreateTourForm(
            validState().copy(
                isRoundTripFareEnabled = false,
                isOutboundFareEnabled = false,
                isReturnFareEnabled = false,
            ),
            ZoneOffset.UTC,
        )

        assertNull(result.trip)
        assertEquals(setOf(CreateTourField.FareConfiguration), result.invalidFields)
    }

    @Test
    fun `ignores optional fare text while its type is disabled`() {
        val result = validateCreateTourForm(
            validState().copy(
                isOutboundFareEnabled = false,
                outboundFare = "not a price",
            ),
            ZoneOffset.UTC,
        )

        assertTrue(result.invalidFields.isEmpty())
        assertNull(checkNotNull(result.trip).outboundFareCents)
    }

    @Test
    fun `venida sin definir mantiene el tour valido sin hora de venida`() {
        val result = validateCreateTourForm(validState(), ZoneOffset.UTC)
        val trip = checkNotNull(result.trip)

        assertTrue(result.invalidFields.isEmpty())
        assertNull(trip.horaVenidaMillis)
        assertEquals(trip.dateMillis, trip.horaSalidaMillis)
    }

    @Test
    fun `venida posterior a salida es valida`() {
        val state = validState().copy(
            hour = 8,
            minute = 0,
            isVenidaDefined = true,
            venidaHour = 18,
            venidaMinute = 30,
        )

        val result = validateCreateTourForm(state, ZoneOffset.UTC)
        val trip = checkNotNull(result.trip)

        assertTrue(result.invalidFields.isEmpty())
        assertEquals(
            LocalDateTime.of(2026, 8, 25, 18, 30)
                .atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli(),
            trip.horaVenidaMillis,
        )
        assertEquals(
            LocalDateTime.of(2026, 8, 25, 8, 0)
                .atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli(),
            trip.horaSalidaMillis,
        )
    }

    @Test
    fun `venida anterior o igual a salida es invalida`() {
        val before = validateCreateTourForm(
            validState().copy(
                hour = 18,
                minute = 0,
                isVenidaDefined = true,
                venidaHour = 8,
                venidaMinute = 0,
            ),
            ZoneOffset.UTC,
        )
        assertNull(before.trip)
        assertTrue(CreateTourField.ReturnTime in before.invalidFields)

        val equal = validateCreateTourForm(
            validState().copy(
                hour = 8,
                minute = 0,
                isVenidaDefined = true,
                venidaHour = 8,
                venidaMinute = 0,
            ),
            ZoneOffset.UTC,
        )
        assertNull(equal.trip)
        assertTrue(CreateTourField.ReturnTime in equal.invalidFields)
    }

    private fun validState() = CreateTourUiState.initial(
        LocalDateTime.of(2026, 8, 25, 10, 30),
    ).copy(
        destination = "Bogotá",
        supervisor = "Luis Pérez",
        seatCount = "40",
        freight = "0",
        roundTripFare = "70",
    )
}
