package com.gestionviajes.feature.trips.domain.model

/**
 * Tramo de un Tour. Reemplaza la idea descartada de cronómetro por asiento:
 * la ocupación de un asiento ya no es única por Tour, sino potencialmente
 * distinta para IDA y para VENIDA.
 *
 * No es un concepto de persistencia nuevo: se deriva del [FareType] ya
 * asignado a cada `BookingSeat`. Ver docs/BUSINESS_RULES.md y
 * docs/DATA_MODEL.md.
 */
enum class TripLeg {
    IDA,
    VENIDA,
}

/** Tramos que ocupa un tipo de pasaje. `Ida y vuelta` ocupa ambos. */
fun FareType.occupiedLegs(): Set<TripLeg> = when (this) {
    FareType.ROUND_TRIP -> setOf(TripLeg.IDA, TripLeg.VENIDA)
    FareType.OUTBOUND -> setOf(TripLeg.IDA)
    FareType.RETURN -> setOf(TripLeg.VENIDA)
}

fun FareType.occupiesLeg(leg: TripLeg): Boolean = leg in occupiedLegs()

/** Dos tipos de pasaje "chocan" en el mismo asiento si comparten algún tramo. */
fun FareType.overlapsWith(other: FareType): Boolean =
    occupiedLegs().intersect(other.occupiedLegs()).isNotEmpty()
