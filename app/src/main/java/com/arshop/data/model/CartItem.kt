package com.arshop.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val userId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val price: Double = 0.0,
    val size: String = "",
    val color: String = "",
    val quantity: Int = 1,
    val addedAt: Timestamp = Timestamp.now()
) {

    fun getTotalPrice(): Double = price * quantity

    fun canIncreaseQuantity(): Boolean = quantity < 10

    fun canDecreaseQuantity(): Boolean = quantity > 1

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "productId" to productId,
            "userId" to userId,
            "productName" to productName,
            "productImage" to productImage,
            "price" to price,
            "size" to size,
            "color" to color,
            "quantity" to quantity,
            "addedAt" to addedAt
        )
    }

    companion object {
        fun fromFirestore(document: DocumentSnapshot): CartItem? {
            return try {
                CartItem(
                    id = document.id,
                    productId = document.getString("productId") ?: "",
                    userId = document.getString("userId") ?: "",
                    productName = document.getString("productName") ?: "",
                    productImage = document.getString("productImage") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    size = document.getString("size") ?: "",
                    color = document.getString("color") ?: "",
                    quantity = (document.getLong("quantity") ?: 1).toInt(),
                    addedAt = document.getTimestamp("addedAt") ?: Timestamp.now()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
