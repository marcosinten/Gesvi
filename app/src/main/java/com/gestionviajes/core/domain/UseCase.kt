package com.gestionviajes.core.domain

/**
 * Clase base para casos de uso que ejecutan una acción única y retornan un resultado.
 *
 * No usen directamente si el resultado debe observarse como Flow — usar [FlowUseCase].
 *
 * Uso:
 * ```kotlin
 * class CreateTripUseCase @Inject constructor(...) : UseCase<Trip, Long>() {
 *     override suspend fun execute(params: Trip): Long = repository.create(params)
 * }
 *
 * // En ViewModel:
 * viewModelScope.launch { createTrip(newTrip) }
 * ```
 */
abstract class UseCase<in P, out R> {
    /** Implementa la lógica de negocio. Llamar desde un scope de coroutine. */
    abstract suspend fun execute(params: P): R

    suspend operator fun invoke(params: P): R = execute(params)
}

/**
 * Marker para casos de uso que no requieren parámetros.
 * Uso: `getTrips(NoParams)`
 */
object NoParams
