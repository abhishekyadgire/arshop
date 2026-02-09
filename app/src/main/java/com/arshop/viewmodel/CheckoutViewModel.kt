package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Address
import com.arshop.data.model.CartItem
import com.arshop.data.model.Order
import com.arshop.data.model.OrderItem
import com.arshop.data.model.PaymentMethod
import com.arshop.repository.CartRepository
import com.arshop.repository.OrderRepository
import com.arshop.repository.UserRepository
import com.arshop.ui.state.CheckoutState
import com.arshop.ui.state.CheckoutStep
import com.arshop.util.PriceUtils
import com.arshop.util.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for checkout process.
 * Manages multi-step checkout flow with address and payment selection.
 */
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _checkoutState = MutableStateFlow(CheckoutState.initial())
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()
    
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Loads checkout data including cart, addresses, and payment methods.
     */
    fun loadCheckoutData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _checkoutState.value = _checkoutState.value.copy(loading = true)
            
            // Load cart items
            cartRepository.getCartItems(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _cartItems.value = result.data
                            val total = result.data.sumOf { it.getTotalPrice() }
                            _checkoutState.value = _checkoutState.value.copy(
                                cartItemCount = result.data.sumOf { it.quantity },
                                cartTotal = total,
                                loading = false
                            )
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message ?: "Failed to load cart"
                            _checkoutState.value = _checkoutState.value.copy(loading = false)
                        }
                        else -> {}
                    }
                }
            
            // Load addresses
            userRepository.getAddresses(userId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _addresses.value = result.data
                            // Auto-select default address
                            val defaultAddress = result.data.find { it.isDefault }
                            if (defaultAddress != null) {
                                selectAddress(defaultAddress)
                            }
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message ?: "Failed to load addresses"
                        }
                        else -> {}
                    }
                }
            
            // Load payment methods (for now, create default ones)
            _paymentMethods.value = listOf(
                PaymentMethod.createDefault()
            )
        }
    }
    
    /**
     * Navigates to the next checkout step.
     */
    fun nextStep() {
        val currentState = _checkoutState.value
        val nextStep = currentState.currentStep.next()
        
        if (nextStep != null && currentState.canProceed()) {
            _checkoutState.value = currentState.copy(currentStep = nextStep)
        } else {
            _error.value = currentState.getValidationError()
        }
    }
    
    /**
     * Navigates to the previous checkout step.
     */
    fun previousStep() {
        val currentState = _checkoutState.value
        val previousStep = currentState.currentStep.previous()
        
        if (previousStep != null && currentState.canGoBack()) {
            _checkoutState.value = currentState.copy(currentStep = previousStep)
        }
    }
    
    /**
     * Selects a shipping address.
     */
    fun selectAddress(address: Address) {
        _checkoutState.value = _checkoutState.value.copy(selectedAddress = address)
    }
    
    /**
     * Selects a payment method.
     */
    fun selectPayment(payment: PaymentMethod) {
        _checkoutState.value = _checkoutState.value.copy(selectedPayment = payment)
    }
    
    /**
     * Places the order.
     */
    fun placeOrder() {
        val userId = auth.currentUser?.uid ?: return
        val state = _checkoutState.value
        
        if (!state.hasAllRequiredInfo()) {
            _error.value = "Please complete all required information"
            return
        }
        
        viewModelScope.launch {
            _checkoutState.value = state.copy(loading = true)
            
            val summary = PriceUtils.calculateOrderSummary(state.cartTotal)
            
            val order = Order(
                userId = userId,
                items = _cartItems.value.map { cartItem ->
                    OrderItem(
                        productId = cartItem.productId,
                        productName = cartItem.productName,
                        productImage = cartItem.productImage,
                        price = cartItem.price,
                        quantity = cartItem.quantity,
                        size = cartItem.size,
                        color = cartItem.color
                    )
                },
                shippingAddress = state.selectedAddress,
                subtotal = summary.subtotal,
                shipping = summary.shipping,
                tax = summary.tax,
                total = summary.total,
                paymentMethod = state.selectedPayment?.displayName ?: "Card",
                paymentStatus = "Pending",
                orderStatus = "Pending"
            )
            
            orderRepository.createOrder(order)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            // Clear cart after successful order
                            cartRepository.clearCart(userId).collect {}
                            
                            _checkoutState.value = _checkoutState.value.copy(
                                loading = false,
                                orderPlaced = true,
                                orderId = result.data.id,
                                currentStep = CheckoutStep.CONFIRMATION
                            )
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message ?: "Failed to place order"
                            _checkoutState.value = _checkoutState.value.copy(loading = false)
                        }
                        else -> {}
                    }
                }
        }
    }
    
    /**
     * Resets checkout state to initial.
     */
    fun resetCheckout() {
        _checkoutState.value = CheckoutState.initial()
        _cartItems.value = emptyList()
        _error.value = null
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
