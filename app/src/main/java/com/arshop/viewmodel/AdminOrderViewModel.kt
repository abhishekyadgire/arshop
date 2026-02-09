package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Order
import com.arshop.repository.AdminRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for admin order management.
 * Handles order listing, status updates, and order details.
 */
@HiltViewModel
class AdminOrderViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()
    
    private val _filterStatus = MutableStateFlow<String?>(null)
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()
    
    private val _filteredOrders = MutableStateFlow<List<Order>>(emptyList())
    val filteredOrders: StateFlow<List<Order>> = _filteredOrders.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()
    
    /**
     * Loads all orders.
     */
    fun loadOrders() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            adminRepository.getAllOrders(limit = 200)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _orders.value = result.data.sortedByDescending { 
                                it.createdAt.seconds 
                            }
                            applyFilter()
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
     * Loads details of a specific order.
     */
    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            val order = _orders.value.find { it.id == orderId }
            if (order != null) {
                _selectedOrder.value = order
            } else {
                _error.value = "Order not found"
            }
        }
    }
    
    /**
     * Updates order status.
     */
    fun updateOrderStatus(
        orderId: String,
        status: String,
        trackingNumber: String? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _updateSuccess.value = false
            
            adminRepository.updateOrderStatus(orderId, status, trackingNumber)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _updateSuccess.value = true
                            loadOrders() // Reload to get updated data
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to update order status"
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
     * Sets filter by status.
     */
    fun setFilterStatus(status: String?) {
        _filterStatus.value = status
        applyFilter()
    }
    
    /**
     * Applies status filter to orders.
     */
    private fun applyFilter() {
        _filteredOrders.value = if (_filterStatus.value != null) {
            _orders.value.filter { it.orderStatus == _filterStatus.value }
        } else {
            _orders.value
        }
    }
    
    /**
     * Gets orders by status.
     */
    fun getOrdersByStatus(status: String): List<Order> {
        return _orders.value.filter { it.orderStatus == status }
    }
    
    /**
     * Gets pending orders.
     */
    fun getPendingOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Pending" }
    }
    
    /**
     * Gets confirmed orders.
     */
    fun getConfirmedOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Confirmed" }
    }
    
    /**
     * Gets processing orders.
     */
    fun getProcessingOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Processing" }
    }
    
    /**
     * Gets shipped orders.
     */
    fun getShippedOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Shipped" }
    }
    
    /**
     * Gets delivered orders.
     */
    fun getDeliveredOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Delivered" }
    }
    
    /**
     * Searches orders by ID or customer name.
     */
    fun searchOrders(query: String): List<Order> {
        if (query.isBlank()) return _filteredOrders.value
        
        return _filteredOrders.value.filter { order ->
            order.id.contains(query, ignoreCase = true) ||
            order.shippingAddress?.fullName?.contains(query, ignoreCase = true) == true
        }
    }
    
    /**
     * Gets order statistics.
     */
    fun getOrderStatistics(): OrderStatistics {
        val orders = _orders.value
        return OrderStatistics(
            total = orders.size,
            pending = orders.count { it.orderStatus == "Pending" },
            confirmed = orders.count { it.orderStatus == "Confirmed" },
            processing = orders.count { it.orderStatus == "Processing" },
            shipped = orders.count { it.orderStatus == "Shipped" },
            delivered = orders.count { it.orderStatus == "Delivered" },
            cancelled = orders.count { it.orderStatus == "Cancelled" },
            totalRevenue = orders.filter { it.orderStatus == "Delivered" }
                .sumOf { it.total }
        )
    }
    
    /**
     * Clears selected order.
     */
    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }
    
    /**
     * Resets update success flag.
     */
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Order statistics data class.
 */
data class OrderStatistics(
    val total: Int = 0,
    val pending: Int = 0,
    val confirmed: Int = 0,
    val processing: Int = 0,
    val shipped: Int = 0,
    val delivered: Int = 0,
    val cancelled: Int = 0,
    val totalRevenue: Double = 0.0
)
