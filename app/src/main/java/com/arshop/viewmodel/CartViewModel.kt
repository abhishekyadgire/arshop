package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.CartItem
import com.arshop.repository.CartRepository
import com.arshop.util.Result
import com.arshop.util.Constants
import com.arshop.util.PriceUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for shopping cart.
 * Manages cart items, quantities, and calculations.
 */
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()
    
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()
    
    private val _shipping = MutableStateFlow(0.0)
    val shipping: StateFlow<Double> = _shipping.asStateFlow()
    
    private val _tax = MutableStateFlow(0.0)
    val tax: StateFlow<Double> = _tax.asStateFlow()
    
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()
    
    /**
     * Loads cart items for the current user.
     */
    fun loadCart() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            cartRepository.getCartItems(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _cartItems.value = result.data
                            calculateTotals()
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load cart"
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
     * Updates quantity of a cart item.
     */
    fun updateQuantity(cartItemId: String, quantity: Int) {
        if (quantity < Constants.Cart.MIN_QUANTITY || 
            quantity > Constants.Cart.MAX_QUANTITY) {
            _error.value = "Quantity must be between ${Constants.Cart.MIN_QUANTITY} and ${Constants.Cart.MAX_QUANTITY}"
            return
        }
        
        viewModelScope.launch {
            cartRepository.updateQuantity(cartItemId, quantity)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // Cart will be updated via real-time listener
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to update quantity"
                        }
                        is Result.Loading -> {}
                    }
                }
        }
    }
    
    /**
     * Increments quantity of a cart item.
     */
    fun incrementQuantity(cartItemId: String) {
        val item = _cartItems.value.find { it.id == cartItemId } ?: return
        if (item.quantity < Constants.Cart.MAX_QUANTITY) {
            updateQuantity(cartItemId, item.quantity + 1)
        }
    }
    
    /**
     * Decrements quantity of a cart item.
     */
    fun decrementQuantity(cartItemId: String) {
        val item = _cartItems.value.find { it.id == cartItemId } ?: return
        if (item.quantity > Constants.Cart.MIN_QUANTITY) {
            updateQuantity(cartItemId, item.quantity - 1)
        }
    }
    
    /**
     * Removes an item from cart.
     */
    fun removeItem(cartItemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(cartItemId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // Cart will be updated via real-time listener
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to remove item"
                        }
                        is Result.Loading -> {}
                    }
                }
        }
    }
    
    /**
     * Clears all items from cart.
     */
    fun clearCart() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            cartRepository.clearCart(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _cartItems.value = emptyList()
                            calculateTotals()
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to clear cart"
                        }
                        is Result.Loading -> {}
                    }
                }
        }
    }
    
    /**
     * Calculates cart totals.
     */
    private fun calculateTotals() {
        val items = _cartItems.value
        val itemsSubtotal = items.sumOf { it.getTotalPrice() }
        val shippingCost = PriceUtils.calculateShipping(itemsSubtotal)
        val taxAmount = PriceUtils.calculateTax(itemsSubtotal)
        val totalAmount = itemsSubtotal + shippingCost + taxAmount
        
        _itemCount.value = items.sumOf { it.quantity }
        _subtotal.value = itemsSubtotal
        _shipping.value = shippingCost
        _tax.value = taxAmount
        _total.value = totalAmount
    }
    
    /**
     * Checks if cart is empty.
     */
    fun isCartEmpty(): Boolean {
        return _cartItems.value.isEmpty()
    }
    
    /**
     * Checks if free shipping is eligible.
     */
    fun isFreeShippingEligible(): Boolean {
        return PriceUtils.isFreeShippingEligible(_subtotal.value)
    }
    
    /**
     * Gets amount needed for free shipping.
     */
    fun getAmountNeededForFreeShipping(): Double {
        return PriceUtils.amountNeededForFreeShipping(_subtotal.value)
    }
    
    /**
     * Syncs cart with Firestore.
     */
    fun syncCart() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            cartRepository.syncCart(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // Sync successful
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to sync cart"
                        }
                        is Result.Loading -> {}
                    }
                }
        }
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
