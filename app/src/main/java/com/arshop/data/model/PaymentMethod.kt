package com.arshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Payment method types supported by the app.
 */
enum class PaymentMethodType {
    STRIPE,
    PAYPAL,
    RAZORPAY,
    GOOGLE_PAY;
    
    fun getDisplayName(): String {
        return when (this) {
            STRIPE -> "Credit/Debit Card"
            PAYPAL -> "PayPal"
            RAZORPAY -> "Razorpay"
            GOOGLE_PAY -> "Google Pay"
        }
    }
    
    fun getIcon(): String {
        return when (this) {
            STRIPE -> "💳"
            PAYPAL -> "🅿️"
            RAZORPAY -> "💰"
            GOOGLE_PAY -> "📱"
        }
    }
}

/**
 * Represents a payment method.
 */
@Parcelize
data class PaymentMethod(
    val id: String = "",
    val type: PaymentMethodType = PaymentMethodType.STRIPE,
    val displayName: String = "",
    val isDefault: Boolean = false,
    val lastFourDigits: String? = null,
    val expiryMonth: Int? = null,
    val expiryYear: Int? = null,
    val cardBrand: String? = null
) : Parcelable {
    
    fun getFormattedCardNumber(): String {
        return lastFourDigits?.let { "•••• $it" } ?: ""
    }
    
    fun getFormattedExpiry(): String {
        return if (expiryMonth != null && expiryYear != null) {
            "%02d/%02d".format(expiryMonth, expiryYear % 100)
        } else {
            ""
        }
    }
    
    fun isExpired(): Boolean {
        if (expiryMonth == null || expiryYear == null) return false
        
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        
        return expiryYear < currentYear || 
               (expiryYear == currentYear && expiryMonth < currentMonth)
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "type" to type.name,
            "displayName" to displayName,
            "isDefault" to isDefault,
            "lastFourDigits" to lastFourDigits,
            "expiryMonth" to expiryMonth,
            "expiryYear" to expiryYear,
            "cardBrand" to cardBrand
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any?>): PaymentMethod {
            val typeString = map["type"] as? String ?: "STRIPE"
            val type = try {
                PaymentMethodType.valueOf(typeString)
            } catch (e: Exception) {
                PaymentMethodType.STRIPE
            }
            
            return PaymentMethod(
                id = map["id"] as? String ?: "",
                type = type,
                displayName = map["displayName"] as? String ?: type.getDisplayName(),
                isDefault = map["isDefault"] as? Boolean ?: false,
                lastFourDigits = map["lastFourDigits"] as? String,
                expiryMonth = (map["expiryMonth"] as? Number)?.toInt(),
                expiryYear = (map["expiryYear"] as? Number)?.toInt(),
                cardBrand = map["cardBrand"] as? String
            )
        }
        
        fun createDefault(): PaymentMethod {
            return PaymentMethod(
                id = "default",
                type = PaymentMethodType.STRIPE,
                displayName = "Credit/Debit Card",
                isDefault = true
            )
        }
    }
}
