package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.CartItem
import com.arshop.data.model.Product
import com.arshop.repository.CartRepository
import com.arshop.repository.ProductRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for product detail screen.
 * Manages product details, size/color selection, and add to cart functionality.
 */
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()
    
    private val _selectedSize = MutableStateFlow<String?>(null)
    val selectedSize: StateFlow<String?> = _selectedSize.asStateFlow()
    
    private val _selectedColor = MutableStateFlow<String?>(null)
    val selectedColor: StateFlow<String?> = _selectedColor.asStateFlow()
    
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _addToCartSuccess = MutableStateFlow(false)
    val addToCartSuccess: StateFlow<Boolean> = _addToCartSuccess.asStateFlow()
    
    private val _stockAvailable = MutableStateFlow(true)
    val stockAvailable: StateFlow<Boolean> = _stockAvailable.asStateFlow()
    
    /**
     * Loads product details by ID.
     */
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            productRepository.getProductById(productId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _product.value = result.data
                            // Auto-select first size and color if available
                            if (_selectedSize.value == null && result.data.sizes.isNotEmpty()) {
                                _selectedSize.value = result.data.sizes.first()
                            }
                            if (_selectedColor.value == null && result.data.colors.isNotEmpty()) {
                                _selectedColor.value = result.data.colors.first().name
                            }
                            updateStockAvailability()
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load product"
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
     * Selects a size.
     */
    fun selectSize(size: String) {
        _selectedSize.value = size
        updateStockAvailability()
    }
    
    /**
     * Selects a color.
     */
    fun selectColor(color: String) {
        _selectedColor.value = color
        updateStockAvailability()
    }
    
    /**
     * Sets quantity.
     */
    fun setQuantity(qty: Int) {
        if (qty >= 1 && qty <= 10) {
            _quantity.value = qty
            updateStockAvailability()
        }
    }
    
    /**
     * Increments quantity.
     */
    fun incrementQuantity() {
        if (_quantity.value < 10) {
            _quantity.value++
            updateStockAvailability()
        }
    }
    
    /**
     * Decrements quantity.
     */
    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value--
            updateStockAvailability()
        }
    }
    
    /**
     * Updates stock availability based on selected options.
     */
    private fun updateStockAvailability() {
        val currentProduct = _product.value ?: return
        val size = _selectedSize.value ?: return
        val color = _selectedColor.value ?: return
        
        val stockCount = currentProduct.getStockCount(size, color)
        _stockAvailable.value = stockCount >= _quantity.value
    }
    
    /**
     * Adds product to cart with selected options.
     */
    fun addToCart() {
        val userId = auth.currentUser?.uid ?: return
        val currentProduct = _product.value
        val size = _selectedSize.value
        val color = _selectedColor.value
        
        if (currentProduct == null) {
            _error.value = "Product not loaded"
            return
        }
        
        if (size == null) {
            _error.value = "Please select a size"
            return
        }
        
        if (color == null) {
            _error.value = "Please select a color"
            return
        }
        
        if (!_stockAvailable.value) {
            _error.value = "Selected item is out of stock"
            return
        }
        
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _addToCartSuccess.value = false
            
            val cartItem = CartItem(
                productId = currentProduct.id,
                userId = userId,
                productName = currentProduct.name,
                productImage = currentProduct.images.firstOrNull() ?: "",
                price = currentProduct.price,
                size = size,
                color = color,
                quantity = _quantity.value
            )
            
            cartRepository.addToCart(cartItem)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _addToCartSuccess.value = true
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to add to cart"
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
     * Checks if product can be added to cart.
     */
    fun canAddToCart(): Boolean {
        return _selectedSize.value != null &&
               _selectedColor.value != null &&
               _stockAvailable.value &&
               !_loading.value
    }
    
    /**
     * Resets add to cart success state.
     */
    fun resetAddToCartSuccess() {
        _addToCartSuccess.value = false
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
