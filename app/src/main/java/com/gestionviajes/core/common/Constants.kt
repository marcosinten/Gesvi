package com.gestionviajes.core.common

/**
 * Constantes globales de la aplicación.
 * Evita "magic strings" dispersas en el código.
 */
object Constants {
    const val DATABASE_NAME    = "app_de_viajes_db"
    const val DATABASE_VERSION = 1

    /** Timeout para StateFlow.WhileSubscribed — evita resubscripciones frecuentes */
    const val FLOW_TIMEOUT_MS = 5_000L
}
