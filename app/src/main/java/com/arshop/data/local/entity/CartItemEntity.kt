package com.arshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arshop.data.model.CartItem
import com.google.firebase.Timestamp

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val userId: String,
    val productName: String,
    val productImage: String,
    val price: Double,
    val size: String,
    val color: String,
    val quantity: Int,
    val addedAt: Long
) {
    fun toCartItem(): CartItem {
        return CartItem(
            id = id,
            productId = productId,
            userId = userId,
            productName = productName,
            productImage = productImage,
            price = price,
            size = size,
            color = color,
            quantity = quantity,
            addedAt = Timestamp(addedAt / 1000, ((addedAt % 1000) * 1000000).toInt())
        )
    }

    companion object {
        fun fromCartItem(cartItem: CartItem): CartItemEntity {
            return CartItemEntity(
                id = cartItem.id,
                productId = cartItem.productId,
                userId = cartItem.userId,
                productName = cartItem.productName,
                productImage = cartItem.productImage,
                price = cartItem.price,
                size = cartItem.size,
                color = cartItem.color,
                quantity = cartItem.quantity,
                addedAt = cartItem.addedAt.seconds * 1000 + cartItem.addedAt.nanoseconds / 1000000
            )
        }
    }
}
