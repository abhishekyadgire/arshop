package com.arshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arshop.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user operations.
 * Provides methods to interact with the users table.
 */
@Dao
interface UserDao {

    /**
     * Get a user by ID.
     * Returns a Flow for reactive updates.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    /**
     * Get a user by ID (suspend version for one-time queries).
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSync(userId: String): UserEntity?

    /**
     * Get a user by email.
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Insert or replace a user.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Update user profile information.
     */
    @Query("UPDATE users SET displayName = :displayName, photoUrl = :photoUrl, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateProfile(userId: String, displayName: String?, photoUrl: String?, updatedAt: Long)

    /**
     * Update user phone number.
     */
    @Query("UPDATE users SET phoneNumber = :phoneNumber, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updatePhoneNumber(userId: String, phoneNumber: String?, updatedAt: Long)

    /**
     * Update user addresses.
     */
    @Query("UPDATE users SET addressesJson = :addressesJson, updatedAt = :updatedAt WHERE id = :userId")
    suspend fun updateAddresses(userId: String, addressesJson: String, updatedAt: Long)

    /**
     * Delete a specific user.
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    /**
     * Clear all users.
     */
    @Query("DELETE FROM users")
    suspend fun clearAll()
}
