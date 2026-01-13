package com.arshop.repository

import com.arshop.data.model.Product
import com.arshop.util.Result
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(limit: Int = 50, lastVisible: DocumentSnapshot? = null): Flow<Result<List<Product>>>
    fun getProductById(productId: String): Flow<Result<Product>>
    fun getProductsByCategory(category: String, gender: String?, limit: Int): Flow<Result<List<Product>>>
    fun searchProducts(query: String): Flow<Result<List<Product>>>
    fun getArEnabledProducts(): Flow<Result<List<Product>>>
    fun syncProducts(): Flow<Result<Unit>>
}

data class ProductFilter(
    val category: String? = null,
    val gender: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val isArEnabled: Boolean? = null,
    val sizes: List<String>? = null
)
