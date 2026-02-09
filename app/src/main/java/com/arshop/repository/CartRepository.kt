package com.arshop.repository

import com.arshop.data.model.CartItem
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for shopping cart operations.
 * Provides methods to manage cart items with Firestore sync and local caching.
 */
interface CartRepository {
    
    /**
     * Get all cart items for the current user.
     * Returns a Flow that emits cart items in real-time.
     */
    fun getCartItems(userId: String): Flow<Result<List<CartItem>>>
    
    /**
     * Add a product to the cart.
     */
    fun addToCart(cartItem: CartItem): Flow<Result<Unit>>
    
    /**
     * Update the quantity of a cart item.
     */
    fun updateQuantity(cartItemId: String, quantity: Int): Flow<Result<Unit>>
    
    /**
     * Remove an item from the cart.
     */
    fun removeFromCart(cartItemId: String): Flow<Result<Unit>>
    
    /**
     * Clear all items from the cart.
     */
    fun clearCart(userId: String): Flow<Result<Unit>>
    
    /**
     * Sync cart with Firestore.
     */
    fun syncCart(userId: String): Flow<Result<Unit>>
}
