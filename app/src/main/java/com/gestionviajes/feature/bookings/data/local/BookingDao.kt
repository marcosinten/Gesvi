package com.gestionviajes.feature.bookings.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.gestionviajes.feature.collections.data.local.PaymentEntity
import com.gestionviajes.feature.trips.domain.model.FareType
import com.gestionviajes.feature.trips.domain.model.overlapsWith
import kotlinx.coroutines.flow.Flow

/**
 * Valida que ningún asiento entrante se superponga en tramo (IDA/VENIDA) con
 * una asignación ya existente en el mismo Tour. Un mismo número de asiento
 * puede pertenecer a Bookings distintos si sus FareType no comparten tramo
 * (por ejemplo, uno Solo ida y otro Solo venida). Ver docs/BUSINESS_RULES.md.
 */
private fun assertNoLegOverlap(existing: List<BookingSeatCrossRef>, incoming: List<BookingSeatCrossRef>) {
    val existingBySeat = existing.groupBy { it.seatNumber }
    incoming.forEach { candidate ->
        val incomingFare = FareType.valueOf(candidate.fareType)
        val conflict = existingBySeat[candidate.seatNumber].orEmpty().any { occupied ->
            FareType.valueOf(occupied.fareType).overlapsWith(incomingFare)
        }
        check(!conflict) {
            "El asiento ${candidate.seatNumber} ya está ocupado en un tramo que se superpone con ${incomingFare}."
        }
    }
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings WHERE tripId = :tripId")
    fun observeBookingsForTrip(tripId: Long): kotlinx.coroutines.flow.Flow<List<BookingEntity>>

    @Query("SELECT booking_seat_cross_ref.* FROM booking_seat_cross_ref INNER JOIN bookings ON bookings.id = bookingId WHERE tripId = :tripId")
    fun observeSeatsForTrip(tripId: Long): Flow<List<BookingSeatCrossRef>>

    @Query("SELECT payments.* FROM payments INNER JOIN bookings ON bookings.id = bookingId WHERE tripId = :tripId")
    fun observePaymentsForTrip(tripId: Long): Flow<List<PaymentEntity>>

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    @Insert
    suspend fun insertBookingSeats(crossRefs: List<BookingSeatCrossRef>)

    /**
     * Asignaciones ya existentes para esos números de asiento en el Tour,
     * con su `fareType`, para poder decidir si se superponen en tramo con
     * una asignación entrante. Ya no basta con saber si el asiento "está
     * ocupado": dos Bookings pueden compartir número de asiento en tramos
     * distintos (IDA vs VENIDA).
     */
    @Query("SELECT booking_seat_cross_ref.* FROM booking_seat_cross_ref INNER JOIN bookings ON bookings.id = bookingId WHERE tripId = :tripId AND seatNumber IN (:seatNumbers)")
    suspend fun crossRefsForSeats(tripId: Long, seatNumbers: List<Int>): List<BookingSeatCrossRef>

    @Query("SELECT EXISTS(SELECT 1 FROM bookings WHERE id = :bookingId AND tripId = :tripId)")
    suspend fun bookingBelongsToTrip(tripId: Long, bookingId: Long): Boolean

    @Insert
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query(
        """
        UPDATE booking_seat_cross_ref
        SET fareType = :fareType
        WHERE bookingId = :bookingId
            AND seatNumber = :seatNumber
            AND EXISTS (
                SELECT 1 FROM bookings
                WHERE bookings.id = :bookingId AND bookings.tripId = :tripId
            )
        """,
    )
    suspend fun updateBookingSeatFare(
        tripId: Long,
        bookingId: Long,
        seatNumber: Int,
        fareType: String,
    ): Int

    @Query(
        """
        DELETE FROM booking_seat_cross_ref
        WHERE bookingId = :bookingId
            AND seatNumber = :seatNumber
            AND EXISTS (
                SELECT 1 FROM bookings
                WHERE bookings.id = :bookingId AND bookings.tripId = :tripId
            )
        """,
    )
    suspend fun deleteBookingSeat(
        tripId: Long,
        bookingId: Long,
        seatNumber: Int,
    ): Int

    @Query(
        """
        DELETE FROM booking_seat_cross_ref
        WHERE bookingId = :bookingId
            AND EXISTS (
                SELECT 1 FROM bookings
                WHERE bookings.id = :bookingId AND bookings.tripId = :tripId
            )
        """,
    )
    suspend fun deleteBookingSeats(
        tripId: Long,
        bookingId: Long,
    ): Int

    @Query(
        """
        DELETE FROM bookings
        WHERE id = :bookingId
            AND tripId = :tripId
        """,
    )
    suspend fun deleteBooking(
        tripId: Long,
        bookingId: Long,
    ): Int

    @Transaction
    suspend fun insertSale(booking: BookingEntity, seats: List<BookingSeatCrossRef>, paymentAmountCents: Long): Long {
        assertNoLegOverlap(crossRefsForSeats(booking.tripId, seats.map(BookingSeatCrossRef::seatNumber)), seats)
        val bookingId = insertBooking(booking)
        insertBookingSeats(seats.map { it.copy(bookingId = bookingId) })
        insertPayment(PaymentEntity(bookingId = bookingId, amountCents = paymentAmountCents, dateMillis = System.currentTimeMillis()))
        return bookingId
    }

    @Transaction
    suspend fun insertReservation(booking: BookingEntity, seats: List<BookingSeatCrossRef>): Long {
        assertNoLegOverlap(crossRefsForSeats(booking.tripId, seats.map(BookingSeatCrossRef::seatNumber)), seats)
        val bookingId = insertBooking(booking)
        insertBookingSeats(seats.map { it.copy(bookingId = bookingId) })
        return bookingId
    }

    @Transaction
    suspend fun addSeatsToBooking(
        tripId: Long,
        bookingId: Long,
        seats: List<BookingSeatCrossRef>,
    ) {
        check(bookingBelongsToTrip(tripId, bookingId)) { "La reserva no pertenece al Tour." }
        assertNoLegOverlap(crossRefsForSeats(tripId, seats.map(BookingSeatCrossRef::seatNumber)), seats)
        insertBookingSeats(seats.map { seat -> seat.copy(bookingId = bookingId) })
    }

    /**
     * Elimina por completo una reserva y sus asignaciones de asiento.
     * Solo aplica a reservas sin abonos (sin `PaymentRecord`), por lo que no
     * hay dinero que conservar. La pertenencia al Tour se valida dentro de la
     * misma transacción: los borrados verifican que el booking exista en el
     * trip indicado.
     */
    @Transaction
    suspend fun deleteBookingWithSeats(
        tripId: Long,
        bookingId: Long,
    ) {
        check(bookingBelongsToTrip(tripId, bookingId)) { "La reserva no pertenece al Tour." }
        deleteBookingSeats(tripId, bookingId)
        check(deleteBooking(tripId, bookingId) == 1) { "Reserva no encontrada" }
    }

    @Transaction
    suspend fun moveBookingSeat(
        tripId: Long,
        bookingId: Long,
        fromSeatNumber: Int,
        toSeatNumber: Int,
        fareType: String,
    ) {
        val destination = BookingSeatCrossRef(bookingId, toSeatNumber, fareType)
        assertNoLegOverlap(crossRefsForSeats(tripId, listOf(toSeatNumber)), listOf(destination))
        check(
            deleteBookingSeat(tripId, bookingId, fromSeatNumber) == 1,
        ) { "No se encontró el asiento origen $fromSeatNumber en la reserva." }
        insertBookingSeats(listOf(destination))
    }

}
