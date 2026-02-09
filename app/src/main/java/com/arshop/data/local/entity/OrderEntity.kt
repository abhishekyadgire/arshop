package com.arshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arshop.data.model.Address
import com.arshop.data.model.Order
import com.arshop.data.model.OrderItem
import com.arshop.data.model.OrderStatusUpdate
import com.google.gson.Gson

/**
 * Room entity for caching order data locally.
 * Provides offline access to order history.
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val itemsJson: String, // Stored as JSON
    val shippingAddressJson: String?, // Stored as JSON
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val orderStatus: String,
    val trackingNumber: String?,
    val statusHistoryJson: String, // Stored as JSON
    val createdAt: Long,
    val updatedAt: Long,
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts the entity to a domain Order model.
     */
    fun toOrder(): Order {
        val gson = Gson()
        
        val items = try {
            gson.fromJson(itemsJson, Array<OrderItem>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
        
        val shippingAddress = try {
            shippingAddressJson?.let {
                gson.fromJson(it, Address::class.java)
            }
        } catch (e: Exception) {
            null
        }
        
        val statusHistory = try {
            gson.fromJson(statusHistoryJson, Array<OrderStatusUpdate>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
        
        return Order(
            id = id,
            userId = userId,
            items = items,
            shippingAddress = shippingAddress,
            subtotal = subtotal,
            shipping = shipping,
            tax = tax,
            total = total,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            orderStatus = orderStatus,
            trackingNumber = trackingNumber,
            statusHistory = statusHistory,
            createdAt = EntityConverters.longToTimestamp(createdAt),
            updatedAt = EntityConverters.longToTimestamp(updatedAt)
        )
    }

    companion object {
        /**
         * Creates an OrderEntity from a domain Order model.
         */
        fun fromOrder(order: Order): OrderEntity {
            val gson = Gson()
            
            return OrderEntity(
                id = order.id,
                userId = order.userId,
                itemsJson = gson.toJson(order.items),
                shippingAddressJson = order.shippingAddress?.let { gson.toJson(it) },
                subtotal = order.subtotal,
                shipping = order.shipping,
                tax = order.tax,
                total = order.total,
                paymentMethod = order.paymentMethod,
                paymentStatus = order.paymentStatus,
                orderStatus = order.orderStatus,
                trackingNumber = order.trackingNumber,
                statusHistoryJson = gson.toJson(order.statusHistory),
                createdAt = EntityConverters.timestampToLong(order.createdAt),
                updatedAt = EntityConverters.timestampToLong(order.updatedAt)
            )
        }
    }
}
