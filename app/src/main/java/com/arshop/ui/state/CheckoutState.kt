package com.arshop.ui.state

import com.arshop.data.model.Address
import com.arshop.data.model.PaymentMethod

/**
 * Represents the steps in the checkout process.
 */
enum class CheckoutStep {
    CART,
    ADDRESS,
    PAYMENT,
    CONFIRMATION;
    
    fun getTitle(): String {
        return when (this) {
            CART -> "Cart"
            ADDRESS -> "Shipping Address"
            PAYMENT -> "Payment"
            CONFIRMATION -> "Confirmation"
        }
    }
    
    fun getStepNumber(): Int {
        return ordinal + 1
    }
    
    fun next(): CheckoutStep? {
        return values().getOrNull(ordinal + 1)
    }
    
    fun previous(): CheckoutStep? {
        return values().getOrNull(ordinal - 1)
    }
    
    fun canProceed(state: CheckoutState): Boolean {
        return when (this) {
            CART -> state.cartItemCount > 0
            ADDRESS -> state.selectedAddress != null
            PAYMENT -> state.selectedPayment != null
            CONFIRMATION -> true
        }
    }
}

/**
 * Represents the complete state of the checkout process.
 */
data class CheckoutState(
    val currentStep: CheckoutStep = CheckoutStep.CART,
    val selectedAddress: Address? = null,
    val selectedPayment: PaymentMethod? = null,
    val cartItemCount: Int = 0,
    val cartTotal: Double = 0.0,
    val loading: Boolean = false,
    val error: String? = null,
    val orderPlaced: Boolean = false,
    val orderId: String? = null
) {
    
    /**
     * Check if can proceed to next step.
     */
    fun canProceed(): Boolean {
        return currentStep.canProceed(this) && !loading
    }
    
    /**
     * Check if can go back to previous step.
     */
    fun canGoBack(): Boolean {
        return currentStep != CheckoutStep.CART && !loading
    }
    
    /**
     * Check if checkout is complete.
     */
    fun isComplete(): Boolean {
        return orderPlaced && orderId != null
    }
    
    /**
     * Get progress percentage (0-100).
     */
    fun getProgressPercentage(): Int {
        val totalSteps = CheckoutStep.values().size
        val currentStepNumber = currentStep.ordinal + 1
        return ((currentStepNumber.toFloat() / totalSteps) * 100).toInt()
    }
    
    /**
     * Check if all required information is provided.
     */
    fun hasAllRequiredInfo(): Boolean {
        return selectedAddress != null && selectedPayment != null && cartItemCount > 0
    }
    
    /**
     * Get validation error for current step.
     */
    fun getValidationError(): String? {
        return when (currentStep) {
            CheckoutStep.CART -> {
                if (cartItemCount == 0) "Your cart is empty" else null
            }
            CheckoutStep.ADDRESS -> {
                if (selectedAddress == null) "Please select a shipping address" else null
            }
            CheckoutStep.PAYMENT -> {
                if (selectedPayment == null) "Please select a payment method" else null
            }
            CheckoutStep.CONFIRMATION -> null
        }
    }
    
    companion object {
        /**
         * Creates an initial checkout state.
         */
        fun initial(): CheckoutState {
            return CheckoutState()
        }
    }
}
