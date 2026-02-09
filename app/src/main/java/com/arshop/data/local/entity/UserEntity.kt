package com.arshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arshop.data.model.Address
import com.arshop.data.model.User
import com.google.firebase.Timestamp
import com.google.gson.Gson

/**
 * Room entity for caching user profile data locally.
 * Provides offline access to user information.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val phoneNumber: String?,
    val addressesJson: String, // Stored as JSON
    val createdAt: Long,
    val updatedAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts the entity to a domain User model.
     */
    fun toUser(): User {
        val gson = Gson()
        
        val addresses = try {
            gson.fromJson(addressesJson, Array<Address>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return User(
            id = id,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            phoneNumber = phoneNumber,
            addresses = addresses,
            createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1000000).toInt()),
            updatedAt = Timestamp(updatedAt / 1000, ((updatedAt % 1000) * 1000000).toInt())
        )
    }

    companion object {
        /**
         * Creates a UserEntity from a domain User model.
         */
        fun fromUser(user: User): UserEntity {
            val gson = Gson()
            
            return UserEntity(
                id = user.id,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl,
                phoneNumber = user.phoneNumber,
                addressesJson = gson.toJson(user.addresses),
                createdAt = user.createdAt.seconds * 1000 + user.createdAt.nanoseconds / 1000000,
                updatedAt = user.updatedAt.seconds * 1000 + user.updatedAt.nanoseconds / 1000000
            )
        }
    }
}
