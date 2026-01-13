package com.arshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val price: Double = 0.0,
    val size: String = "",
    val color: String = "",
    val quantity: Int = 1
) : Parcelable {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "productId" to productId,
            "productName" to productName,
            "productImage" to productImage,
            "price" to price,
            "size" to size,
            "color" to color,
            "quantity" to quantity
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): OrderItem {
            return OrderItem(
                productId = map["productId"] as? String ?: "",
                productName = map["productName"] as? String ?: "",
                productImage = map["productImage"] as? String ?: "",
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                size = map["size"] as? String ?: "",
                color = map["color"] as? String ?: "",
                quantity = (map["quantity"] as? Number)?.toInt() ?: 1
            )
        }
    }
}
