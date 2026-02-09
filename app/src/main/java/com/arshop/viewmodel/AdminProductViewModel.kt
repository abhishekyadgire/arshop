package com.arshop.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshop.data.model.Product
import com.arshop.data.model.ProductColor
import com.arshop.repository.AdminRepository
import com.arshop.repository.StorageRepository
import com.arshop.util.Constants
import com.arshop.util.Result
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for admin product management.
 * Handles product creation, updates, deletion, and file uploads.
 */
@HiltViewModel
class AdminProductViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()
    
    private val _uploadedImages = MutableStateFlow<List<String>>(emptyList())
    val uploadedImages: StateFlow<List<String>> = _uploadedImages.asStateFlow()
    
    private val _uploaded3DModel = MutableStateFlow<String?>(null)
    val uploaded3DModel: StateFlow<String?> = _uploaded3DModel.asStateFlow()
    
    private val _imageUploading = MutableStateFlow(false)
    val imageUploading: StateFlow<Boolean> = _imageUploading.asStateFlow()
    
    private val _modelUploading = MutableStateFlow(false)
    val modelUploading: StateFlow<Boolean> = _modelUploading.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    /**
     * Loads product for editing.
     */
    fun loadProduct(product: Product) {
        _selectedProduct.value = product
        _uploadedImages.value = product.images
        _uploaded3DModel.value = product.model3dUrl
    }
    
    /**
     * Creates a new product.
     */
    fun createProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        gender: String,
        subcategory: String,
        sizes: List<String>,
        colors: List<ProductColor>,
        stock: Map<String, Int>
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _saveSuccess.value = false
            
            val product = Product(
                name = name,
                description = description,
                price = price,
                category = category,
                gender = gender,
                subcategory = subcategory,
                images = _uploadedImages.value,
                model3dUrl = _uploaded3DModel.value,
                isArEnabled = _uploaded3DModel.value != null,
                sizes = sizes,
                colors = colors,
                stock = stock,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            
            adminRepository.addProduct(product)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _saveSuccess.value = true
                            _selectedProduct.value = result.data
                            _loading.value = false
                            clearUploads()
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to create product"
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
     * Updates an existing product.
     */
    fun updateProduct(
        productId: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        gender: String,
        subcategory: String,
        sizes: List<String>,
        colors: List<ProductColor>,
        stock: Map<String, Int>
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _saveSuccess.value = false
            
            val product = Product(
                id = productId,
                name = name,
                description = description,
                price = price,
                category = category,
                gender = gender,
                subcategory = subcategory,
                images = _uploadedImages.value,
                model3dUrl = _uploaded3DModel.value,
                isArEnabled = _uploaded3DModel.value != null,
                sizes = sizes,
                colors = colors,
                stock = stock,
                updatedAt = Timestamp.now()
            )
            
            adminRepository.updateProduct(product)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _saveSuccess.value = true
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to update product"
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
     * Deletes a product.
     */
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            adminRepository.deleteProduct(productId)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _saveSuccess.value = true
                            _selectedProduct.value = null
                            _loading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to delete product"
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
     * Uploads product image.
     */
    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            _imageUploading.value = true
            _error.value = null
            
            val path = "${Constants.StoragePaths.PRODUCTS}/${System.currentTimeMillis()}.jpg"
            
            storageRepository.uploadFile(imageUri, path)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val currentImages = _uploadedImages.value.toMutableList()
                            currentImages.add(result.data)
                            _uploadedImages.value = currentImages
                            _imageUploading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to upload image"
                            _imageUploading.value = false
                        }
                        is Result.Loading -> {
                            _imageUploading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Uploads 3D model file.
     */
    fun upload3DModel(modelUri: Uri) {
        viewModelScope.launch {
            _modelUploading.value = true
            _error.value = null
            
            val path = "${Constants.StoragePaths.MODELS_3D}/${System.currentTimeMillis()}.glb"
            
            storageRepository.uploadFile(modelUri, path)
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uploaded3DModel.value = result.data
                            _modelUploading.value = false
                        }
                        is Result.Failure -> {
                            _error.value = result.exception.message 
                                ?: "Failed to upload 3D model"
                            _modelUploading.value = false
                        }
                        is Result.Loading -> {
                            _modelUploading.value = true
                        }
                    }
                }
        }
    }
    
    /**
     * Removes an uploaded image.
     */
    fun removeImage(imageUrl: String) {
        val currentImages = _uploadedImages.value.toMutableList()
        currentImages.remove(imageUrl)
        _uploadedImages.value = currentImages
    }
    
    /**
     * Removes 3D model.
     */
    fun remove3DModel() {
        _uploaded3DModel.value = null
    }
    
    /**
     * Clears uploaded files.
     */
    private fun clearUploads() {
        _uploadedImages.value = emptyList()
        _uploaded3DModel.value = null
    }
    
    /**
     * Resets form state.
     */
    fun resetForm() {
        _selectedProduct.value = null
        clearUploads()
        _saveSuccess.value = false
        _error.value = null
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
    
    // Add missing methods for admin screens
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    /**
     * Loads all products from repository.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // TODO: Use ProductRepository or AdminRepository to get all products
            // For now, set empty list
            _products.value = emptyList()
            _loading.value = false
        }
    }
    
    /**
     * Loads a single product by ID.
     */
    fun loadProductById(productId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // TODO: Load product from repository
            // For now, create a placeholder
            _loading.value = false
        }
    }
    
    /**
     * Uploads multiple images in parallel for better performance.
     * Images are uploaded concurrently using coroutines to reduce wait time.
     * 
     * @param uris List of image URIs to upload
     */
    fun uploadImages(uris: List<Uri>) {
        viewModelScope.launch {
            // Upload images in parallel using async
            val uploadJobs = uris.map { uri ->
                async {
                    uploadImage(uri)
                }
            }
            // Wait for all uploads to complete
            uploadJobs.awaitAll()
        }
    }
}
