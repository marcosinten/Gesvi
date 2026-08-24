package com.gestionviajes.feature.collections.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE bookingId = :bookingId")
    fun observePaymentsForBooking(bookingId: Long): Flow<List<PaymentEntity>>

    @Insert
    suspend fun insertPayment(payment: PaymentEntity): Long
}
