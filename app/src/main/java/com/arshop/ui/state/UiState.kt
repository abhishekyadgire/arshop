package com.arshop.ui.state

/**
 * Generic UI state wrapper for composables.
 * Represents the three common states: Loading, Success, and Error.
 */
sealed class UiState<out T> {
    /**
     * Represents a loading state.
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Represents a successful state with data.
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Represents an error state with a message.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    
    /**
     * Idle state (initial state before any action).
     */
    object Idle : UiState<Nothing>()
    
    /**
     * Check if the state is loading.
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Check if the state is successful.
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Check if the state is error.
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Check if the state is idle.
     */
    val isIdle: Boolean
        get() = this is Idle
    
    /**
     * Get data if state is Success, null otherwise.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Get error message if state is Error, null otherwise.
     */
    fun errorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
    
    companion object {
        /**
         * Creates a Loading state.
         */
        fun <T> loading(): UiState<T> = Loading
        
        /**
         * Creates a Success state with data.
         */
        fun <T> success(data: T): UiState<T> = Success(data)
        
        /**
         * Creates an Error state with a message.
         */
        fun <T> error(message: String, throwable: Throwable? = null): UiState<T> = 
            Error(message, throwable)
        
        /**
         * Creates an Idle state.
         */
        fun <T> idle(): UiState<T> = Idle
    }
}

/**
 * Extension function to map UiState data.
 */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
    return when (this) {
        is UiState.Loading -> UiState.Loading
        is UiState.Success -> UiState.Success(transform(data))
        is UiState.Error -> UiState.Error(message, throwable)
        is UiState.Idle -> UiState.Idle
    }
}

/**
 * Extension function to execute action on success.
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) {
        action(data)
    }
    return this
}

/**
 * Extension function to execute action on error.
 */
inline fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) {
        action(message)
    }
    return this
}

/**
 * Extension function to execute action on loading.
 */
inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) {
        action()
    }
    return this
}
