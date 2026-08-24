package com.gestionviajes.core.domain

import com.gestionviajes.core.common.Result
import com.gestionviajes.core.common.extensions.asResult
import kotlinx.coroutines.flow.Flow

/**
 * Clase base para casos de uso que exponen un [Flow] observable.
 *
 * Envuelve automáticamente cada emisión en [Result] y captura excepciones.
 *
 * Uso:
 * ```kotlin
 * class GetTripsUseCase @Inject constructor(...) : FlowUseCase<NoParams, List<Trip>>() {
 *     override fun execute(params: NoParams): Flow<List<Trip>> = repository.observeAll()
 * }
 *
 * // En ViewModel:
 * val uiState = getTrips(NoParams)
 *     .map { result -> ... }
 *     .stateIn(...)
 * ```
 */
abstract class FlowUseCase<in P, out R> {
    abstract fun execute(params: P): Flow<R>

    operator fun invoke(params: P): Flow<Result<R>> = execute(params).asResult()
}
