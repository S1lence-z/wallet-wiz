package com.example.walletwiz.utils

/**
 * A sealed class that represents the result of an operation.
 * It can be either a Success with data or an Error with an exception.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with a [data] payload.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an [exception].
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Represents an ongoing operation (e.g., for loading states).
     * This is optional and can be used if you need to represent an explicit loading state
     * within the Result stream, though often loading is handled separately in the ViewModel.
     */
    data object Loading : Result<Nothing>()
}
