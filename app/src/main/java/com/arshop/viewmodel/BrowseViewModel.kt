package com.arshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Product
import com.arshop.repository.ProductFilter
import com.arshop.repository.ProductRepository
import com.arshop.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for browsing and filtering products.
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _selectedGender = MutableStateFlow<String?>(null)
    val selectedGender: StateFlow<String?> = _selectedGender.asStateFlow()
    
    private val _priceRange = MutableStateFlow<Pair<Double, Double>?>(null)
    val priceRange: StateFlow<Pair<Double, Double>?> = _priceRange.asStateFlow()
    
    private val _arEnabledOnly = MutableStateFlow(false)
    val arEnabledOnly: StateFlow<Boolean> = _arEnabledOnly.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private var searchJob: Job? = null
    
    init {
        loadProducts()
    }
    
    /**
     * Loads all products.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            productRepository.getAllProducts(limit = 100)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _products.value = result.data
                            applyFilters()
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to load products"
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
     * Searches products by query.
     */
    fun search(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            applyFilters()
            return
        }
        
        searchJob = viewModelScope.launch {
            _loading.value = true
            
            productRepository.searchProducts(query)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _products.value = result.data
                            applyFilters()
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Search failed"
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
     * Sets category filter.
     */
    fun setCategory(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }
    
    /**
     * Sets gender filter.
     */
    fun setGender(gender: String?) {
        _selectedGender.value = gender
        applyFilters()
    }
    
    /**
     * Sets price range filter.
     */
    fun setPriceRange(minPrice: Double, maxPrice: Double) {
        _priceRange.value = Pair(minPrice, maxPrice)
        applyFilters()
    }
    
    /**
     * Toggles AR-enabled filter.
     */
    fun setArEnabledOnly(enabled: Boolean) {
        _arEnabledOnly.value = enabled
        applyFilters()
    }
    
    /**
     * Applies all active filters to products.
     */
    private fun applyFilters() {
        var filtered = _products.value
        
        // Apply category filter
        _selectedCategory.value?.let { category ->
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        
        // Apply gender filter
        _selectedGender.value?.let { gender ->
            filtered = filtered.filter { it.gender.equals(gender, ignoreCase = true) }
        }
        
        // Apply price range filter
        _priceRange.value?.let { (min, max) ->
            filtered = filtered.filter { it.price in min..max }
        }
        
        // Apply AR-enabled filter
        if (_arEnabledOnly.value) {
            filtered = filtered.filter { it.isArEnabled }
        }
        
        _filteredProducts.value = filtered
    }
    
    /**
     * Clears all filters.
     */
    fun clearFilters() {
        _selectedCategory.value = null
        _selectedGender.value = null
        _priceRange.value = null
        _arEnabledOnly.value = false
        _searchQuery.value = ""
        applyFilters()
    }
    
    /**
     * Checks if any filters are active.
     */
    fun hasActiveFilters(): Boolean {
        return _selectedCategory.value != null ||
               _selectedGender.value != null ||
               _priceRange.value != null ||
               _arEnabledOnly.value ||
               _searchQuery.value.isNotBlank()
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}
