package com.gestionviajes.feature.trips.domain.usecase

import com.gestionviajes.core.domain.FlowUseCase
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.TourBookingSeat
import com.gestionviajes.feature.trips.domain.model.TourBookingSummary
import com.gestionviajes.feature.trips.domain.model.TourPayment
import com.gestionviajes.feature.trips.domain.model.TourSeat
import com.gestionviajes.feature.trips.domain.model.TourSeatStatus
import com.gestionviajes.feature.trips.domain.model.occupiesLeg
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Obtiene el Tour y genera exactamente los asientos definidos por su capacidad.
 *
 * La ocupación mostrada por número de asiento es la del tramo activo
 * (`Trip.currentLeg()`), calculada al leer el estado, nunca por un job en
 * segundo plano. Antes de la hora de venida se muestra la ocupación de IDA;
 * desde la hora de venida en adelante se muestra la de VENIDA, y si no existe
 * una reserva/venta de VENIDA para ese asiento, se muestra disponible aunque
 * haya tenido pasajero en IDA. El historial y los pagos de cada Booking no
 * se ven afectados por este cambio de proyección: solo cambia qué Booking se
 * considera "el que ocupa el asiento ahora mismo".
 */
class GetTourDetailUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
) : FlowUseCase<Long, TourDetail?>() {

    override fun execute(params: Long): Flow<TourDetail?> =
        combine(tripRepository.observeTrip(params), bookingRepository.observeBookingsForTrip(params)) { trip, bookings ->
            trip?.let {
                val bookingSummaries = bookings.map { booking ->
                    val seats = booking.seats.map { seat ->
                        TourBookingSeat(
                            number = seat.number,
                            fareType = seat.fareType,
                            fareCents = it.fareCents(seat.fareType) ?: 0,
                        )
                    }
                    val totalCents = seats.sumOf(TourBookingSeat::fareCents)
                    val receivedCents = booking.payments.sumOf { payment -> payment.amountCents }
                    TourBookingSummary(
                        id = booking.id,
                        responsibleName = booking.passengerName,
                        seats = seats,
                        totalCents = totalCents,
                        receivedCents = receivedCents,
                        status = when {
                            booking.payments.isEmpty() -> TourSeatStatus.RESERVED
                            receivedCents < totalCents -> TourSeatStatus.PARTIAL
                            else -> TourSeatStatus.PAID
                        },
                        payments = booking.payments
                            .sortedBy { payment -> payment.dateMillis }
                            .map { payment ->
                                TourPayment(
                                    amountCents = payment.amountCents,
                                    dateMillis = payment.dateMillis,
                                )
                            },
                    )
                }
                val activeLeg = it.currentLeg()
                val occupancyBySeat = bookingSummaries.flatMap { booking ->
                    booking.seats
                        .filter { seat -> seat.fareType.occupiesLeg(activeLeg) }
                        .map { seat ->
                            seat.number to SeatOccupancy(status = booking.status, bookingId = booking.id)
                        }
                }.toMap()
                TourDetail(
                    trip = it,
                    seats = (1..it.seatCount).map { number ->
                        occupancyBySeat[number]?.let { occupancy ->
                            TourSeat(
                                number = number,
                                status = occupancy.status,
                                bookingId = occupancy.bookingId,
                            )
                        } ?: TourSeat(number = number, status = TourSeatStatus.EMPTY)
                    },
                    bookings = bookingSummaries,
                )
            }
        }
}

private data class SeatOccupancy(
    val status: TourSeatStatus,
    val bookingId: Long,
)
