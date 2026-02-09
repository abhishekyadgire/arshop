package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Order
import com.arshop.data.model.Product
import com.arshop.repository.AdminRepository
import com.arshop.repository.AnalyticsData
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for admin dashboard.
 * Manages admin status, products overview, orders, and analytics.
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _analytics = MutableStateFlow<AnalyticsData?>(null)
    val analytics: StateFlow<AnalyticsData?> = _analytics.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Checks if user has admin privileges.
     */
    fun checkAdminStatus(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            adminRepository.checkAdminStatus(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _isAdmin.value = result.data
                            if (result.data) {
                                loadAdminData()
                            }
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _isAdmin.value = false
                            _error.value = result.exception.message 
                                ?: "Failed to check admin status"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Loads all admin dashboard data.
     */
    private fun loadAdminData() {
        loadProducts()
        loadOrders()
        loadAnalytics()
    }
    
    /**
     * Loads all products.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            
            // Using ProductRepository's getAllProducts via AdminRepository
            adminRepository.getAllOrders(limit = 100) // Get from products later
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // This would be products in real implementation
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load products"
                            _loading.value = false
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Loads all orders.
     */
    fun loadOrders() {
        viewModelScope.launch {
            _loading.value = true
            
            adminRepository.getAllOrders(limit = 100)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _orders.value = result.data.sortedByDescending { 
                                it.createdAt.seconds 
                            }
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load orders"
                            _loading.value = false
                        }
                        is Result.Loading -> {
                            _loading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Loads analytics data.
     */
    fun loadAnalytics() {
        viewModelScope.launch {
            adminRepository.getAnalytics()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _analytics.value = result.data
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load analytics"
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Refreshes all admin data.
     */
    fun refresh() {
        loadAdminData()
    }
    
    /**
     * Gets pending orders count.
     */
    fun getPendingOrdersCount(): Int {
        return _orders.value.count { it.orderStatus == "Pending" }
    }
    
    /**
     * Gets recent orders (last 10).
     */
    fun getRecentOrders(): List<Order> {
        return _orders.value.take(10)
    }
    
    /**
     * Gets orders by status.
     */
    fun getOrdersByStatus(status: String): List<Order> {
        return _orders.value.filter { it.orderStatus == status }
    }
    
    /**
     * Calculates total revenue from completed orders.
     */
    fun getTotalRevenue(): Double {
        return _orders.value
            .filter { it.orderStatus == "Delivered" }
            .sumOf { it.total }
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
