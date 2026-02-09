package com.arshop.repository

import com.arshop.data.local.dao.ProductDao
import com.arshop.data.local.entity.ProductEntity
import com.arshop.data.model.Product
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository.
 * Provides hybrid caching strategy with Firestore and Room.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override fun getAllProducts(
        limit: Int,
        lastVisible: DocumentSnapshot?
    ): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // First, emit cached data if available
            val cachedProducts = productDao.getAllProducts().firstOrNull()
                ?.map { it.toProduct() } ?: emptyList()
            
            if (cachedProducts.isNotEmpty()) {
                emit(Result.Success(cachedProducts))
            }

            // Then fetch from Firestore
            var query: Query = firestore.collection(FirestoreCollections.PRODUCTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())

            lastVisible?.let {
                query = query.startAfter(it)
            }

            val snapshot = query.get().await()
            val products = snapshot.documents.mapNotNull { Product.fromFirestore(it) }

            // Cache products
            val entities = products.map { ProductEntity.fromProduct(it) }
            productDao.insertProducts(entities)

            emit(Result.Success(products))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load products: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getProductById(productId: String): Flow<Result<Product>> = flow {
        try {
            emit(Result.Loading)

            // First check cache
            val cachedProduct = productDao.getProductById(productId)
            if (cachedProduct != null) {
                emit(Result.Success(cachedProduct.toProduct()))
            }

            // Fetch from Firestore
            val document = firestore.collection(FirestoreCollections.PRODUCTS)
                .document(productId)
                .get()
                .await()

            val product = Product.fromFirestore(document)
                ?: throw Exception("Product not found")

            // Update cache
            productDao.insertProduct(ProductEntity.fromProduct(product))

            emit(Result.Success(product))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load product: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getProductsByCategory(
        category: String,
        gender: String?,
        limit: Int
    ): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // Build Firestore query
            var query: Query = firestore.collection(FirestoreCollections.PRODUCTS)
                .whereEqualTo("category", category)

            gender?.let {
                query = query.whereEqualTo("gender", it)
            }

            query = query.limit(limit.toLong())

            val snapshot = query.get().await()
            val products = snapshot.documents.mapNotNull { Product.fromFirestore(it) }

            // Cache products
            val entities = products.map { ProductEntity.fromProduct(it) }
            productDao.insertProducts(entities)

            emit(Result.Success(products))
        } catch (e: Exception) {
            // Fallback to cached data
            val cachedProducts = productDao.getProductsByCategory(category)
            cachedProducts.map { entities ->
                entities.map { it.toProduct() }
            }.collect { products ->
                if (products.isNotEmpty()) {
                    emit(Result.Success(products))
                } else {
                    emit(Result.Failure(Exception("Failed to load products: ${e.message}")))
                }
            }
        }
    }.flowOn(ioDispatcher)

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // Search in Firestore (limited text search)
            val snapshot = firestore.collection(FirestoreCollections.PRODUCTS)
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val firestoreProducts = snapshot.documents.mapNotNull { Product.fromFirestore(it) }

            // Also search in cache
            val cachedProducts = productDao.searchProducts(query).firstOrNull()
                ?.map { it.toProduct() } ?: emptyList()
            
            // Combine results (remove duplicates)
            val allProducts = (firestoreProducts + cachedProducts)
                .distinctBy { it.id }
                .filter { it.name.contains(query, ignoreCase = true) }

            emit(Result.Success(allProducts))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to search products: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getArEnabledProducts(): Flow<Result<List<Product>>> = flow {
        try {
            emit(Result.Loading)

            // Fetch from Firestore
            val snapshot = firestore.collection(FirestoreCollections.PRODUCTS)
                .whereEqualTo("isArEnabled", true)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { Product.fromFirestore(it) }

            // Cache products
            val entities = products.map { ProductEntity.fromProduct(it) }
            productDao.insertProducts(entities)

            emit(Result.Success(products))
        } catch (e: Exception) {
            // Fallback to cache
            productDao.getArEnabledProducts().map { entities ->
                entities.map { it.toProduct() }
            }.collect { cachedProducts ->
                if (cachedProducts.isNotEmpty()) {
                    emit(Result.Success(cachedProducts))
                } else {
                    emit(Result.Failure(Exception("Failed to load AR products: ${e.message}")))
                }
            }
        }
    }.flowOn(ioDispatcher)

    override fun syncProducts(): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch all products from Firestore
            val snapshot = firestore.collection(FirestoreCollections.PRODUCTS)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { Product.fromFirestore(it) }

            // Clear cache and insert new data
            productDao.clearAll()
            val entities = products.map { ProductEntity.fromProduct(it) }
            productDao.insertProducts(entities)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to sync products: ${e.message}")))
        }
    }.flowOn(ioDispatcher)
}
