package com.arshop.ui.state

import com.arshop.data.model.User

/**
 * Represents the authentication state of the app.
 */
sealed class AuthState {
    /**
     * Initial state before any authentication action.
     */
    object Idle : AuthState()
    
    /**
     * Authentication process is in progress.
     */
    object Loading : AuthState()
    
    /**
     * User is authenticated.
     */
    data class Authenticated(val user: User) : AuthState()
    
    /**
     * User is not authenticated.
     */
    object Unauthenticated : AuthState()
    
    /**
     * Authentication error occurred.
     */
    data class Error(val message: String) : AuthState()
    
    /**
     * Check if currently loading.
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Check if authenticated.
     */
    val isAuthenticated: Boolean
        get() = this is Authenticated
    
    /**
     * Check if unauthenticated.
     */
    val isUnauthenticated: Boolean
        get() = this is Unauthenticated
    
    /**
     * Get the authenticated user if available.
     */
    fun getUserOrNull(): User? = when (this) {
        is Authenticated -> user
        else -> null
    }
    
    /**
     * Get error message if in error state.
     */
    fun getErrorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
}
