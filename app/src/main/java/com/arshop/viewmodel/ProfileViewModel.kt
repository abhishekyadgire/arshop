package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Address
import com.arshop.data.model.User
import com.arshop.repository.AuthRepository
import com.arshop.repository.UserRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for user profile management.
 * Handles profile updates, address management, and user settings.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()
    
    /**
     * Loads user profile.
     */
    fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            userRepository.getUserProfile(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _user.value = result.data
                            loadAddresses(userId)
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load profile"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Loads user addresses.
     */
    private fun loadAddresses(userId: String) {
        viewModelScope.launch {
            userRepository.getAddresses(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _addresses.value = result.data
                        }
                        is Result.Failure -> {
                            // Don't override main error if loading profile failed
                            if (_error.value == null) {
                                _error.value = result.exception.message 
                                    ?: "Failed to load addresses"
                            }
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Updates user profile information.
     */
    fun updateProfile(
        userId: String,
        displayName: String? = null,
        photoUrl: String? = null,
        phoneNumber: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _updateSuccess.value = false
            
            userRepository.updateUserProfile(userId, displayName, photoUrl, phoneNumber)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _user.value = result.data
                            _updateSuccess.value = true
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to update profile"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Adds or updates an address.
     */
    fun addOrUpdateAddress(userId: String, address: Address) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            userRepository.updateAddress(userId, address)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            loadAddresses(userId)
                            _updateSuccess.value = true
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to save address"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Deletes an address.
     */
    fun deleteAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            userRepository.removeAddress(userId, addressId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            loadAddresses(userId)
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to delete address"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Sets an address as default.
     */
    fun setDefaultAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.setDefaultAddress(userId, addressId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            loadAddresses(userId)
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to set default address"
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Signs out the current user.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _user.value = null
                            _addresses.value = emptyList()
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message ?: "Failed to sign out"
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Resets update success flag.
     */
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
