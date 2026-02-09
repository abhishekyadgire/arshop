package com.arshop.repository

import com.arshop.data.model.Order
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for order operations.
 * Provides methods to manage orders with Firestore and local caching.
 */
interface OrderRepository {
    
    /**
     * Create a new order.
     */
    fun createOrder(order: Order): Flow<Result<Order>>
    
    /**
     * Get an order by ID.
     */
    fun getOrderById(orderId: String): Flow<Result<Order>>
    
    /**
     * Get all orders for a user.
     */
    fun getUserOrders(userId: String): Flow<Result<List<Order>>>
    
    /**
     * Update order status.
     */
    fun updateOrderStatus(orderId: String, status: String, trackingNumber: String? = null): Flow<Result<Unit>>
    
    /**
     * Cancel an order.
     */
    fun cancelOrder(orderId: String): Flow<Result<Unit>>
    
    /**
     * Sync orders with Firestore.
     */
    fun syncOrders(userId: String): Flow<Result<Unit>>
}
