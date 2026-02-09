package com.arshop.repository

import com.arshop.data.model.Order
import com.arshop.data.model.Product
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for admin operations.
 * Provides methods for product management, order management, and analytics.
 */
interface AdminRepository {
    
    /**
     * Check if a user has admin privileges.
     */
    fun checkAdminStatus(userId: String): Flow<Result<Boolean>>
    
    /**
     * Add a new product to the catalog.
     */
    fun addProduct(product: Product): Flow<Result<Product>>
    
    /**
     * Update an existing product.
     */
    fun updateProduct(product: Product): Flow<Result<Unit>>
    
    /**
     * Delete a product from the catalog.
     */
    fun deleteProduct(productId: String): Flow<Result<Unit>>
    
    /**
     * Get all orders (admin view).
     */
    fun getAllOrders(limit: Int = 50): Flow<Result<List<Order>>>
    
    /**
     * Update order status (admin operation).
     */
    fun updateOrderStatus(orderId: String, status: String, trackingNumber: String? = null): Flow<Result<Unit>>
    
    /**
     * Get analytics data.
     */
    fun getAnalytics(): Flow<Result<AnalyticsData>>
}

/**
 * Data class representing analytics information.
 */
data class AnalyticsData(
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalProducts: Int = 0,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val cancelledOrders: Int = 0
)
