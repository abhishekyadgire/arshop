package com.arshop.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "email" to email,
            "displayName" to displayName,
            "photoUrl" to photoUrl,
            "phoneNumber" to phoneNumber,
            "addresses" to addresses.map { it.toMap() },
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromFirestore(document: DocumentSnapshot): User? {
            return try {
                User(
                    id = document.id,
                    email = document.getString("email") ?: "",
                    displayName = document.getString("displayName"),
                    photoUrl = document.getString("photoUrl"),
                    phoneNumber = document.getString("phoneNumber"),
                    addresses = (document.get("addresses") as? List<*>)?.mapNotNull { addressMap ->
                        (addressMap as? Map<*, *>)?.let { map ->
                            @Suppress("UNCHECKED_CAST")
                            Address.fromMap(map as Map<String, Any?>)
                        }
                    } ?: emptyList(),
                    createdAt = document.getTimestamp("createdAt") ?: Timestamp.now(),
                    updatedAt = document.getTimestamp("updatedAt") ?: Timestamp.now()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
