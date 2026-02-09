package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Category
import com.arshop.data.model.Product
import com.arshop.repository.ProductRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home screen.
 * Manages featured products and categories.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    val featuredProducts: StateFlow<List<Product>> = _featuredProducts.asStateFlow()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    /**
     * Loads featured products and categories.
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            loadFeaturedProducts()
            loadCategories()
            
            _loading.value = false
        }
    }
    
    /**
     * Loads featured products (AR-enabled and high-rated products).
     */
    fun loadFeaturedProducts() {
        viewModelScope.launch {
            productRepository.getArEnabledProducts()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _featuredProducts.value = result.data.take(10)
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load featured products"
                        }
                        is Result.Loading -> {
                            // Loading handled by parent
                        }
                    }
                }
        }
    }
    
    /**
     * Loads product categories.
     */
    fun loadCategories() {
        viewModelScope.launch {
            // Create default categories
            _categories.value = listOf(
                Category(
                    id = "clothing",
                    name = "Clothing",
                    imageUrl = "",
                    productCount = 0
                ),
                Category(
                    id = "footwear",
                    name = "Footwear",
                    imageUrl = "",
                    productCount = 0
                ),
                Category(
                    id = "men",
                    name = "Men",
                    imageUrl = "",
                    productCount = 0
                ),
                Category(
                    id = "women",
                    name = "Women",
                    imageUrl = "",
                    productCount = 0
                )
            )
        }
    }
    
    /**
     * Refreshes home screen data.
     */
    fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            _error.value = null
            
            loadFeaturedProducts()
            loadCategories()
            
            _refreshing.value = false
        }
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
