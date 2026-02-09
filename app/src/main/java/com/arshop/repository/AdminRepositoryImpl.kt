package com.arshop.repository

import com.arshop.data.model.Order
import com.arshop.data.model.OrderStatusUpdate
import com.arshop.data.model.Product
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdminRepository.
 * Provides administrative operations for product and order management.
 */
@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AdminRepository {

    override fun checkAdminStatus(userId: String): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)

            // Check if user exists in admin_users collection
            val document = firestore.collection(FirestoreCollections.ADMIN_USERS)
                .document(userId)
                .get()
                .await()

            val isAdmin = document.exists() && document.getBoolean("isActive") == true

            emit(Result.Success(isAdmin))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to check admin status: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun addProduct(product: Product): Flow<Result<Product>> = flow {
        try {
            emit(Result.Loading)

            // Validate product data
            if (product.name.isBlank()) {
                throw Exception("Product name is required")
            }
            if (product.price <= 0) {
                throw Exception("Product price must be greater than 0")
            }

            // Create product in Firestore
            val productRef = if (product.id.isNotEmpty()) {
                firestore.collection(FirestoreCollections.PRODUCTS).document(product.id)
            } else {
                firestore.collection(FirestoreCollections.PRODUCTS).document()
            }

            val productWithId = product.copy(
                id = productRef.id,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            productRef.set(productWithId.toMap()).await()

            emit(Result.Success(productWithId))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to add product: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateProduct(product: Product): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Validate product data
            if (product.id.isBlank()) {
                throw Exception("Product ID is required")
            }
            if (product.name.isBlank()) {
                throw Exception("Product name is required")
            }
            if (product.price <= 0) {
                throw Exception("Product price must be greater than 0")
            }

            // Update product in Firestore
            val productWithTimestamp = product.copy(updatedAt = Timestamp.now())
            
            firestore.collection(FirestoreCollections.PRODUCTS)
                .document(product.id)
                .set(productWithTimestamp.toMap())
                .await()

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update product: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun deleteProduct(productId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Delete product from Firestore
            firestore.collection(FirestoreCollections.PRODUCTS)
                .document(productId)
                .delete()
                .await()

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to delete product: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getAllOrders(limit: Int): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)

            // Fetch all orders
            val snapshot = firestore.collection(FirestoreCollections.ORDERS)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { Order.fromFirestore(it) }

            emit(Result.Success(orders))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load orders: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateOrderStatus(
        orderId: String,
        status: String,
        trackingNumber: String?
    ): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch current order
            val document = firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .get()
                .await()

            val order = Order.fromFirestore(document)
                ?: throw Exception("Order not found")

            // Create status update
            val statusUpdate = OrderStatusUpdate(
                status = status,
                timestamp = Timestamp.now(),
                note = "Updated by admin"
            )

            // Build updates
            val updates = mutableMapOf<String, Any>(
                "orderStatus" to status,
                "updatedAt" to Timestamp.now(),
                "statusHistory" to (order.statusHistory + statusUpdate).map { it.toMap() }
            )

            trackingNumber?.let {
                updates["trackingNumber"] = it
            }

            // Update payment status based on order status
            when (status) {
                "Delivered" -> updates["paymentStatus"] = "Completed"
                "Cancelled" -> updates["paymentStatus"] = "Refunded"
            }

            firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .update(updates)
                .await()

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update order status: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getAnalytics(): Flow<Result<AnalyticsData>> = flow {
        try {
            emit(Result.Loading)

            // Get all orders
            val ordersSnapshot = firestore.collection(FirestoreCollections.ORDERS)
                .get()
                .await()

            val orders = ordersSnapshot.documents.mapNotNull { Order.fromFirestore(it) }

            // Get all products
            val productsSnapshot = firestore.collection(FirestoreCollections.PRODUCTS)
                .get()
                .await()

            // Calculate analytics
            val totalOrders = orders.size
            val totalRevenue = orders.filter { it.orderStatus != "Cancelled" }
                .sumOf { it.total }
            val totalProducts = productsSnapshot.size()
            val pendingOrders = orders.count { it.orderStatus == "Pending" }
            val completedOrders = orders.count { it.orderStatus == "Delivered" }
            val cancelledOrders = orders.count { it.orderStatus == "Cancelled" }

            val analytics = AnalyticsData(
                totalOrders = totalOrders,
                totalRevenue = totalRevenue,
                totalProducts = totalProducts,
                pendingOrders = pendingOrders,
                completedOrders = completedOrders,
                cancelledOrders = cancelledOrders
            )

            emit(Result.Success(analytics))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load analytics: ${e.message}")))
        }
    }.flowOn(ioDispatcher)
}
