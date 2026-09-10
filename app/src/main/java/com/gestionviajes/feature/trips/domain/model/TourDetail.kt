package com.gestionviajes.feature.trips.domain.model

enum class TourSeatStatus {
    EMPTY,
    RESERVED,
    PARTIAL,
    PAID,
}

data class TourSeat(
    val number: Int,
    val status: TourSeatStatus,
    val bookingId: Long? = null,
) {
    init {
        require(number > 0)
        require((status == TourSeatStatus.EMPTY) == (bookingId == null))
    }
}

data class TourBookingSeat(
    val number: Int,
    val fareType: FareType,
    val fareCents: Long,
)

data class TourPayment(
    val amountCents: Long,
    val dateMillis: Long,
)

/** Resumen económico descartable del Tour calculado desde sus reservas y abonos actuales. */
data class TourAccountsSummary(
    val freightCents: Long,
    val totalSoldCents: Long,
    val collectedCents: Long,
    val bookings: List<TourBookingSummary>,
) {
    /**
     * Pendiente por cobrar del Tour: suma de los pendientes individuales de
     * cada reserva, sin compensar el déficit de unas con el exceso de otras
     * (ver "Cuentas Del Tour" en docs/BUSINESS_RULES.md).
     */
    val pendingCollectionCents: Long
        get() = bookings.sumOf(TourBookingSummary::pendingCents)

    val remainingToCoverFreightCents: Long
        get() = (freightCents - collectedCents).coerceAtLeast(0)

    val currentProfitCents: Long
        get() = (collectedCents - freightCents).coerceAtLeast(0)
}

/** Resumen descartable de un Booking para los consumidores del Detalle del Tour. */
data class TourBookingSummary(
    val id: Long,
    val responsibleName: String,
    val seats: List<TourBookingSeat>,
    val totalCents: Long,
    val receivedCents: Long,
    val status: TourSeatStatus,
    val payments: List<TourPayment> = emptyList(),
) {
    val pendingCents: Long
        get() = (totalCents - receivedCents).coerceAtLeast(0)

    val creditCents: Long
        get() = (receivedCents - totalCents).coerceAtLeast(0)
}

/** Proyección descartable del Tour y sus asientos configurados. */
data class TourDetail(
    val trip: Trip,
    val seats: List<TourSeat>,
    val bookings: List<TourBookingSummary> = emptyList(),
) {
    init {
        require(seats.map(TourSeat::number) == (1..trip.seatCount).toList())
        require(bookings.map(TourBookingSummary::id).distinct().size == bookings.size)
    }

    val usedSeatCount: Int
        get() = seats.count { seat -> seat.status != TourSeatStatus.EMPTY }

    val availableSeatCount: Int
        get() = seats.size - usedSeatCount

    val accounts: TourAccountsSummary
        get() = TourAccountsSummary(
            freightCents = trip.freightCents,
            totalSoldCents = bookings.sumOf(TourBookingSummary::totalCents),
            collectedCents = bookings.sumOf(TourBookingSummary::receivedCents),
            bookings = bookings,
        )
}
