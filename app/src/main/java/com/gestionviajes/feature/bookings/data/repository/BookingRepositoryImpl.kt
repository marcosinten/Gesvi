package com.gestionviajes.feature.bookings.data.repository

import com.gestionviajes.feature.bookings.data.local.BookingDao
import com.gestionviajes.feature.bookings.data.local.BookingEntity
import com.gestionviajes.feature.bookings.data.local.BookingSeatCrossRef
import com.gestionviajes.feature.bookings.domain.model.Booking
import com.gestionviajes.feature.bookings.domain.model.BookingSeat
import com.gestionviajes.feature.bookings.domain.model.PaymentRecord
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import com.gestionviajes.feature.collections.data.local.PaymentEntity
import com.gestionviajes.feature.trips.domain.model.FareType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class BookingRepositoryImpl @Inject constructor(private val dao: BookingDao) : BookingRepository {
    override fun observeBookingsForTrip(tripId: Long): Flow<List<Booking>> = combine(
        dao.observeBookingsForTrip(tripId), dao.observeSeatsForTrip(tripId), dao.observePaymentsForTrip(tripId),
    ) { bookings, seats, payments ->
        bookings.map { booking ->
            Booking(
                id = booking.id, tripId = booking.tripId, passengerName = booking.passengerName,
                seats = seats.filter { it.bookingId == booking.id }.map { BookingSeat(it.seatNumber, FareType.valueOf(it.fareType)) },
                payments = payments.filter { it.bookingId == booking.id }.map { PaymentRecord(it.amountCents, it.dateMillis) },
            )
        }
    }

    override suspend fun createSale(tripId: Long, responsibleName: String, seats: List<BookingSeat>, amountCents: Long): Long =
        dao.insertSale(BookingEntity(tripId = tripId, passengerName = responsibleName), seats.map { BookingSeatCrossRef(0, it.number, it.fareType.name) }, amountCents)

    override suspend fun createReservation(tripId: Long, responsibleName: String, seats: List<BookingSeat>): Long =
        dao.insertReservation(BookingEntity(tripId = tripId, passengerName = responsibleName), seats.map { BookingSeatCrossRef(0, it.number, it.fareType.name) })

    override suspend fun addSeatsToBooking(
        tripId: Long,
        bookingId: Long,
        seats: List<BookingSeat>,
    ) {
        dao.addSeatsToBooking(
            tripId = tripId,
            bookingId = bookingId,
            seats = seats.map { seat ->
                BookingSeatCrossRef(
                    bookingId = bookingId,
                    seatNumber = seat.number,
                    fareType = seat.fareType.name,
                )
            },
        )
    }

    override suspend fun registerPayment(bookingId: Long, amountCents: Long): Long =
        dao.insertPayment(
            PaymentEntity(
                bookingId = bookingId,
                amountCents = amountCents,
                dateMillis = System.currentTimeMillis(),
            ),
        )

    override suspend fun changeBookingSeatFare(
        tripId: Long,
        bookingId: Long,
        seatNumber: Int,
        fareType: FareType,
    ) {
        check(
            dao.updateBookingSeatFare(
                tripId = tripId,
                bookingId = bookingId,
                seatNumber = seatNumber,
                fareType = fareType.name,
            ) == 1,
        )
    }

    override suspend fun releaseBookingSeat(
        tripId: Long,
        bookingId: Long,
        seatNumber: Int,
    ) {
        check(
            dao.deleteBookingSeat(
                tripId = tripId,
                bookingId = bookingId,
                seatNumber = seatNumber,
            ) == 1,
        )
    }

    override suspend fun deleteBooking(
        tripId: Long,
        bookingId: Long,
    ) {
        dao.deleteBookingWithSeats(tripId, bookingId)
    }

    override suspend fun moveBookingSeat(
        tripId: Long,
        bookingId: Long,
        fromSeatNumber: Int,
        toSeatNumber: Int,
        fareType: FareType,
    ) {
        dao.moveBookingSeat(
            tripId = tripId,
            bookingId = bookingId,
            fromSeatNumber = fromSeatNumber,
            toSeatNumber = toSeatNumber,
            fareType = fareType.name,
        )
    }

}
