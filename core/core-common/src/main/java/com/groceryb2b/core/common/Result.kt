package com.groceryb2b.core.common

/**
 * A generic wrapper for the outcome of a repository/use-case call.
 *
 * Every network or database operation in the app should return one of
 * these instead of throwing, so the UI layer can render Loading / Success
 * / Error states consistently.
 */
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message)
    return this
}
