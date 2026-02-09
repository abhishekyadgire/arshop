package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Order
import com.arshop.data.model.OrderItem
import com.arshop.repository.CartRepository
import com.arshop.repository.OrderRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for order history and order details.
 */
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _cancelSuccess = MutableStateFlow(false)
    val cancelSuccess: StateFlow<Boolean> = _cancelSuccess.asStateFlow()
    
    /**
     * Loads user's order history.
     */
    fun loadOrders() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            orderRepository.getUserOrders(userId)
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
     * Loads details of a specific order.
     */
    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            orderRepository.getOrderById(orderId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _selectedOrder.value = result.data
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load order details"
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
     * Cancels an order.
     */
    fun cancelOrder(orderId: String) {
        val order = _orders.value.find { it.id == orderId } 
            ?: _selectedOrder.value
        
        if (order == null) {
            _error.value = "Order not found"
            return
        }
        
        if (!order.canCancel()) {
            _error.value = "Order cannot be cancelled at this stage"
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _cancelSuccess.value = false
            
            orderRepository.cancelOrder(orderId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _cancelSuccess.value = true
                            // Reload orders to get updated status
                            _selectedOrder.value?.userId?.let { userId ->
                                loadOrders(userId)
                            }
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to cancel order"
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
     * Reorders items from a previous order.
     */
    fun reorder(orderId: String) {
        val userId = auth.currentUser?.uid ?: return
        val order = _selectedOrder.value ?: return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // Add all items from the order to cart
            var successCount = 0
            var failureCount = 0
            
            order.items.forEach { orderItem ->
                val cartItem = com.arshop.data.model.CartItem(
                    productId = orderItem.productId,
                    userId = userId,
                    productName = orderItem.productName,
                    productImage = orderItem.productImage,
                    price = orderItem.price,
                    size = orderItem.size,
                    color = orderItem.color,
                    quantity = orderItem.quantity
                )
                
                cartRepository.addToCart(cartItem)
                    .collect { result ->
                        when (result) {
                            is Result.Success -> successCount++
                            is Result.Failure -> failureCount++
                            else -> {}
                        }
                    }
            }
            
            _loading.value = false
            
            if (failureCount > 0) {
                _error.value = "Some items could not be added to cart"
            }
        }
    }
    
    /**
     * Filters orders by status.
     */
    fun filterOrdersByStatus(status: String): List<Order> {
        return _orders.value.filter { it.orderStatus == status }
    }
    
    /**
     * Gets pending orders.
     */
    fun getPendingOrders(): List<Order> {
        return _orders.value.filter { 
            it.orderStatus in listOf("Pending", "Confirmed", "Processing") 
        }
    }
    
    /**
     * Gets completed orders.
     */
    fun getCompletedOrders(): List<Order> {
        return _orders.value.filter { it.orderStatus == "Delivered" }
    }
    
    /**
     * Gets cancelled orders.
     */
    fun getCancelledOrders(): List<Order> {
        return _orders.value.filter { 
            it.orderStatus in listOf("Cancelled", "Refunded") 
        }
    }
    
    /**
     * Syncs orders with Firestore.
     */
    fun syncOrders(userId: String) {
        viewModelScope.launch {
            orderRepository.syncOrders(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            loadOrders(userId)
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to sync orders"
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Clears selected order.
     */
    fun clearSelectedOrder() {
        _selectedOrder.value = null
    }
    
    /**
     * Resets cancel success flag.
     */
    fun resetCancelSuccess() {
        _cancelSuccess.value = false
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
