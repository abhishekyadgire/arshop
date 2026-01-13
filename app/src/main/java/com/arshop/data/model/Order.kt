package com.arshop.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    val id: String = generateOrderId(),
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val shippingAddress: Address? = null,
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: String = "Pending",
    val orderStatus: String = "Pending",
    val trackingNumber: String? = null,
    val statusHistory: List<OrderStatusUpdate> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {

    fun getItemCount(): Int = items.sumOf { it.quantity }

    fun canCancel(): Boolean = orderStatus in listOf("Pending", "Confirmed")

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "items" to items.map { it.toMap() },
            "shippingAddress" to shippingAddress?.toMap(),
            "subtotal" to subtotal,
            "shipping" to shipping,
            "tax" to tax,
            "total" to total,
            "paymentMethod" to paymentMethod,
            "paymentStatus" to paymentStatus,
            "orderStatus" to orderStatus,
            "trackingNumber" to trackingNumber,
            "statusHistory" to statusHistory.map { it.toMap() },
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        private fun generateOrderId(): String {
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
            val dateString = dateFormat.format(Date())
            val randomSuffix = (10000..99999).random()
            return "ORD-$dateString-$randomSuffix"
        }

        fun fromFirestore(document: DocumentSnapshot): Order? {
            return try {
                Order(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    items = (document.get("items") as? List<*>)?.mapNotNull { itemMap ->
                        (itemMap as? Map<*, *>)?.let { map ->
                            @Suppress("UNCHECKED_CAST")
                            OrderItem.fromMap(map as Map<String, Any?>)
                        }
                    } ?: emptyList(),
                    shippingAddress = (document.get("shippingAddress") as? Map<*, *>)?.let { map ->
                        @Suppress("UNCHECKED_CAST")
                        Address.fromMap(map as Map<String, Any?>)
                    },
                    subtotal = document.getDouble("subtotal") ?: 0.0,
                    shipping = document.getDouble("shipping") ?: 0.0,
                    tax = document.getDouble("tax") ?: 0.0,
                    total = document.getDouble("total") ?: 0.0,
                    paymentMethod = document.getString("paymentMethod") ?: "",
                    paymentStatus = document.getString("paymentStatus") ?: "Pending",
                    orderStatus = document.getString("orderStatus") ?: "Pending",
                    trackingNumber = document.getString("trackingNumber"),
                    statusHistory = (document.get("statusHistory") as? List<*>)?.mapNotNull { statusMap ->
                        (statusMap as? Map<*, *>)?.let { map ->
                            @Suppress("UNCHECKED_CAST")
                            OrderStatusUpdate.fromMap(map as Map<String, Any?>)
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
