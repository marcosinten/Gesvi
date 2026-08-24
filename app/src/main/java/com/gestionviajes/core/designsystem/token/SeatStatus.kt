package com.gestionviajes.core.designsystem.token

/**
 * Todos los posibles estados visuales de una celda de asiento.
 *
 * El color de cada estado se define en [AppColors] y se aplica en [SeatCell].
 * Nunca usar colores crudos en pantallas — siempre pasar por este enum + [AppTheme].
 *
 * Regla arquitectónica:
 * - Este enum vive en el design system (core/designsystem).
 * - Los modelos de dominio (Seat) referencian este enum.
 * - Los Composables leen el estado del ViewModel y mapean a este enum en la UI.
 */
enum class SeatStatus {
    /** Sin reserva asociada. Disponible para venta. */
    Empty,

    /** Reservado pero sin ningún abono registrado. */
    Reserved,

    /** Tiene al menos un abono pero no cubre el total. */
    Partial,

    /** Cubierto completamente — el total de abonos iguala o supera la tarifa. */
    Paid,

    /** Seleccionado por el usuario en el mapa (estado de interacción transitoria). */
    Selected,

    /** No disponible: asiento del conductor, pasillo, avería, etc. */
    Disabled,
}
