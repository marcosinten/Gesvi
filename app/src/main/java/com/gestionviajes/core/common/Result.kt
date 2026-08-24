package com.gestionviajes.core.common

/**
 * Wrapper genérico para resultados de operaciones del dominio.
 *
 * Evita que las excepciones propaguen por capas.
 * ViewModels consumen [Result] y mapean a [UiState].
 *
 * Uso típico:
 * ```kotlin
 * getTrips(NoParams)          // Flow<Result<List<Trip>>>
 *     .collect { result ->
 *         when (result) {
 *             is Result.Loading -> showSpinner()
 *             is Result.Success -> showList(result.data)
 *             is Result.Error   -> showError(result.message)
 *         }
 *     }
 * ```
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val exception: Throwable,
        val message: String? = exception.localizedMessage,
    ) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean   get() = this is Error
    val isLoading: Boolean get() = this is Loading
}

/** Transforma el valor en [Result.Success]. Deja los demás casos sin cambio. */
inline fun <T, R> Result<T>.mapSuccess(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error   -> this
    is Result.Loading -> this
}

/** Retorna el valor si es [Result.Success], o null en cualquier otro caso. */
fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data
