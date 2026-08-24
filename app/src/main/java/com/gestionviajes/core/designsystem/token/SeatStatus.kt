package com.gestionviajes.core.designsystem.token

/**
 * Todos los posibles estados visuales de una celda de asiento.
 *
 * El color se resuelve mediante los tokens de `AppTheme` y se aplica en `SeatCell`.
 * La selección y otras interacciones son estado separado de Presentation.
 */
enum class SeatStatus {
    /** Sin reserva asociada. Disponible para venta. */
    Empty,

    /** Reservado pero sin ningún abono registrado. */
    Reserved,

    /** Tiene al menos un abono pero no cubre el total. */
    Partial,

    /** Cubierto completamente: el total de abonos iguala o supera la tarifa. */
    Paid,
}
