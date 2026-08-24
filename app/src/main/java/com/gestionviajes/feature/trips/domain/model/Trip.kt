package com.gestionviajes.feature.trips.domain.model

/** Modelo de dominio del Tour. */
data class Trip(
    val id: Long = 0,
    /** Puede faltar únicamente en Tours conservados desde el esquema v2. */
    val destination: String?,
    /** Puede faltar únicamente en Tours conservados desde el esquema v2. */
    val supervisor: String?,
    val dateMillis: Long,
    val seatCount: Int,
    val freightCents: Long,
    val roundTripFareCents: Long? = null,
    val outboundFareCents: Long? = null,
    val returnFareCents: Long? = null,
    val isRoundTripFareEnabled: Boolean = true,
    val isOutboundFareEnabled: Boolean = false,
    val isReturnFareEnabled: Boolean = false,
    /** Hora de salida del tramo IDA, como instante absoluto (fecha + hora). */
    val horaSalidaMillis: Long? = null,
    /**
     * Hora de venida del tramo VENIDA, como instante absoluto (fecha + hora),
     * no solo una hora del reloj. Esto es intencional: el viaje puede cruzar
     * la medianoche (salida de noche, venida al día siguiente), y una hora
     * suelta sin fecha sería ambigua para decidir el tramo activo.
     */
    val horaVenidaMillis: Long? = null,
) {
    init {
        require(seatCount > 0)
        require(freightCents >= 0)
        require(activeFareTypes.isNotEmpty())
        require(!isRoundTripFareEnabled || (roundTripFareCents ?: 0) > 0)
        require(!isOutboundFareEnabled || (outboundFareCents ?: 0) > 0)
        require(!isReturnFareEnabled || (returnFareCents ?: 0) > 0)
        require(roundTripFareCents == null || roundTripFareCents > 0)
        require(outboundFareCents == null || outboundFareCents > 0)
        require(returnFareCents == null || returnFareCents > 0)
        require(
            horaSalidaMillis == null || horaVenidaMillis == null || horaVenidaMillis > horaSalidaMillis,
        ) { "La hora de venida debe ser posterior a la hora de salida." }
    }

    /**
     * Tramo activo en este momento. Es un valor calculado, nunca persistido
     * ni disparado por un job en segundo plano:
     * - Antes de [horaVenidaMillis] (o si no está definida), el tramo activo es IDA.
     * - Desde que llega [horaVenidaMillis] en adelante, el tramo activo pasa a VENIDA.
     */
    fun currentLeg(nowMillis: Long = System.currentTimeMillis()): TripLeg =
        if (horaVenidaMillis == null || nowMillis < horaVenidaMillis) TripLeg.IDA else TripLeg.VENIDA

    val activeFareTypes: Set<FareType>
        get() = buildSet {
            if (isRoundTripFareEnabled) add(FareType.ROUND_TRIP)
            if (isOutboundFareEnabled) add(FareType.OUTBOUND)
            if (isReturnFareEnabled) add(FareType.RETURN)
        }

    fun fareCents(fareType: FareType): Long? = when (fareType) {
        FareType.ROUND_TRIP -> roundTripFareCents
        FareType.OUTBOUND -> outboundFareCents
        FareType.RETURN -> returnFareCents
    }

    /** Una sola tarifa activa evita una decisión innecesaria al reservar o vender. */
    val automaticallySelectedFareType: FareType?
        get() = activeFareTypes.singleOrNull()
}
