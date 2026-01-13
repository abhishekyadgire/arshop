package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.User
import com.arshop.repository.AuthRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        authRepository.getCurrentUser()
            .onEach { user ->
                _currentUser.value = user
            }
            .launchIn(viewModelScope)
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signInWithEmail(email, password)
                .collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> AuthUiState.Loading
                        is Result.Success -> AuthUiState.Success(result.data)
                        is Result.Failure -> AuthUiState.Error(
                            result.exception.message ?: "Sign in failed"
                        )
                    }
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            authRepository.signUpWithEmail(email, password, displayName)
                .collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> AuthUiState.Loading
                        is Result.Success -> AuthUiState.Success(result.data)
                        is Result.Failure -> AuthUiState.Error(
                            result.exception.message ?: "Sign up failed"
                        )
                    }
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> AuthUiState.Loading
                        is Result.Success -> AuthUiState.Success(result.data)
                        is Result.Failure -> AuthUiState.Error(
                            result.exception.message ?: "Google Sign-In failed"
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = AuthUiState.Idle
                            _currentUser.value = null
                        }
                        is Result.Failure -> {
                            _uiState.value = AuthUiState.Error(
                                result.exception.message ?: "Sign out failed"
                            )
                        }
                        else -> {}
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
                .collect { result ->
                    _uiState.value = when (result) {
                        is Result.Loading -> AuthUiState.Loading
                        is Result.Success -> AuthUiState.Error("Password reset email sent")
                        is Result.Failure -> AuthUiState.Error(
                            result.exception.message ?: "Failed to send reset email"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }

    // Input validation
    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Please enter a valid email address"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    fun validateDisplayName(displayName: String): String? {
        return when {
            displayName.isBlank() -> "Display name is required"
            displayName.length < 2 -> "Display name must be at least 2 characters"
            displayName.length > 50 -> "Display name must be less than 50 characters"
            else -> null
        }
    }
}
