package com.arshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Address(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String = "",
    val addressLine1: String = "",
    val addressLine2: String? = null,
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "United States",
    val phoneNumber: String = "",
    val isDefault: Boolean = false
) : Parcelable {

    fun isValid(): Boolean {
        return fullName.isNotBlank() &&
                addressLine1.isNotBlank() &&
                city.isNotBlank() &&
                state.isNotBlank() &&
                zipCode.isNotBlank() &&
                phoneNumber.isNotBlank()
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fullName" to fullName,
            "addressLine1" to addressLine1,
            "addressLine2" to addressLine2,
            "city" to city,
            "state" to state,
            "zipCode" to zipCode,
            "country" to country,
            "phoneNumber" to phoneNumber,
            "isDefault" to isDefault
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Address {
            return Address(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                fullName = map["fullName"] as? String ?: "",
                addressLine1 = map["addressLine1"] as? String ?: "",
                addressLine2 = map["addressLine2"] as? String,
                city = map["city"] as? String ?: "",
                state = map["state"] as? String ?: "",
                zipCode = map["zipCode"] as? String ?: "",
                country = map["country"] as? String ?: "United States",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                isDefault = map["isDefault"] as? Boolean ?: false
            )
        }
    }
}
