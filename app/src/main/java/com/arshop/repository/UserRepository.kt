package com.arshop.repository

import com.arshop.data.model.Address
import com.arshop.data.model.User
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile operations.
 * Provides methods to manage user data with Firestore and local caching.
 */
interface UserRepository {
    
    /**
     * Get user profile by user ID.
     */
    fun getUserProfile(userId: String): Flow<Result<User>>
    
    /**
     * Update user profile information.
     */
    fun updateUserProfile(
        userId: String,
        displayName: String? = null,
        photoUrl: String? = null,
        phoneNumber: String? = null
    ): Flow<Result<User>>
    
    /**
     * Add or update an address.
     */
    fun updateAddress(userId: String, address: Address): Flow<Result<Unit>>
    
    /**
     * Remove an address.
     */
    fun removeAddress(userId: String, addressId: String): Flow<Result<Unit>>
    
    /**
     * Get all addresses for a user.
     */
    fun getAddresses(userId: String): Flow<Result<List<Address>>>
    
    /**
     * Set default address.
     */
    fun setDefaultAddress(userId: String, addressId: String): Flow<Result<Unit>>
}
