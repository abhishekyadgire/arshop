package com.arshop.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isFailure: Boolean
        get() = this is Failure

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun exceptionOrNull(): Exception? = when (this) {
        is Failure -> exception
        else -> null
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun failure(exception: Exception): Result<Nothing> = Failure(exception)
        fun loading(): Result<Nothing> = Loading
    }
}
