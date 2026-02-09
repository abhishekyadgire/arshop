package com.arshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arshop.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for order operations.
 * Provides methods to interact with the orders table.
 */
@Dao
interface OrderDao {

    /**
     * Get all orders for a specific user.
     * Returns a Flow for reactive updates.
     */
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserOrders(userId: String): Flow<List<OrderEntity>>

    /**
     * Get a specific order by ID.
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    /**
     * Get orders by status for a user.
     */
    @Query("SELECT * FROM orders WHERE userId = :userId AND orderStatus = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(userId: String, status: String): Flow<List<OrderEntity>>

    /**
     * Insert or replace an order.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    /**
     * Insert or replace multiple orders.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    /**
     * Update order status.
     */
    @Query("UPDATE orders SET orderStatus = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: Long)

    /**
     * Delete a specific order.
     */
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)

    /**
     * Clear all orders for a user.
     */
    @Query("DELETE FROM orders WHERE userId = :userId")
    suspend fun clearUserOrders(userId: String)

    /**
     * Clear all orders.
     */
    @Query("DELETE FROM orders")
    suspend fun clearAll()

    /**
     * Get order count for a user.
     */
    @Query("SELECT COUNT(*) FROM orders WHERE userId = :userId")
    suspend fun getOrderCount(userId: String): Int
}
