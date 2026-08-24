package com.gestionviajes.core.common.extensions

import com.gestionviajes.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Convierte un [Flow]<T> en [Flow]<[Result]<T>>.
 *
 * - Cada emisión se envuelve en [Result.Success].
 * - Cualquier excepción se captura y emite como [Result.Error].
 *
 * Uso:
 * ```kotlin
 * repository.observeAll().asResult()  // Flow<Result<List<Trip>>>
 * ```
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    map<T, Result<T>> { Result.Success(it) }
        .catch { emit(Result.Error(it)) }
