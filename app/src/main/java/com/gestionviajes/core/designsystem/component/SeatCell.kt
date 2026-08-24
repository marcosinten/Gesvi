package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.core.designsystem.token.SeatStatus

/**
 * Celda visual de un asiento en el mapa.
 *
 * Utiliza [SeatStatus] para determinar su color desde [AppTheme.extendedColors].
 * TODO: Implementar UI final y onClick.
 */
@Composable
fun SeatCell(
    number: String,
    status: SeatStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        SeatStatus.Empty -> AppTheme.extendedColors.seatEmpty
        SeatStatus.Reserved -> AppTheme.extendedColors.seatReserved
        SeatStatus.Partial -> AppTheme.extendedColors.seatPartial
        SeatStatus.Paid -> AppTheme.extendedColors.seatPaid
        SeatStatus.Selected -> AppTheme.extendedColors.seatSelected
        SeatStatus.Disabled -> AppTheme.extendedColors.seatDisabled
    }

    Box(
        modifier = modifier.background(backgroundColor)
    ) {
        Text(
            text = number,
            color = AppTheme.extendedColors.onSeat
        )
    }
}
