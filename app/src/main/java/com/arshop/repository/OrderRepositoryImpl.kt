package com.arshop.repository

import com.arshop.data.local.dao.OrderDao
import com.arshop.data.local.entity.OrderEntity
import com.arshop.data.model.Order
import com.arshop.data.model.OrderStatusUpdate
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of OrderRepository.
 * Provides order management with Firestore and offline caching.
 */
@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val orderDao: OrderDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OrderRepository {

    override fun createOrder(order: Order): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)

            // Create order in Firestore
            val orderRef = firestore.collection(FirestoreCollections.ORDERS)
                .document(order.id)

            orderRef.set(order.toMap()).await()

            // Cache locally
            orderDao.insertOrder(OrderEntity.fromOrder(order))

            emit(Result.Success(order))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to create order: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)

            // First check cache
            val cachedOrder = orderDao.getOrderById(orderId)
            if (cachedOrder != null) {
                emit(Result.Success(cachedOrder.toOrder()))
            }

            // Fetch from Firestore
            val document = firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .get()
                .await()

            val order = Order.fromFirestore(document)
                ?: throw Exception("Order not found")

            // Update cache
            orderDao.insertOrder(OrderEntity.fromOrder(order))

            emit(Result.Success(order))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load order: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getUserOrders(userId: String): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)

            // First emit cached data
            orderDao.getUserOrders(userId).map { entities ->
                entities.map { it.toOrder() }
            }.collect { cachedOrders ->
                if (cachedOrders.isNotEmpty()) {
                    emit(Result.Success(cachedOrders))
                }
            }

            // Fetch from Firestore
            val snapshot = firestore.collection(FirestoreCollections.ORDERS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { Order.fromFirestore(it) }

            // Update cache
            val entities = orders.map { OrderEntity.fromOrder(it) }
            orderDao.insertOrders(entities)

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
                note = null
            )

            // Update order
            val updates = mutableMapOf<String, Any>(
                "orderStatus" to status,
                "updatedAt" to Timestamp.now(),
                "statusHistory" to (order.statusHistory + statusUpdate).map { it.toMap() }
            )

            trackingNumber?.let {
                updates["trackingNumber"] = it
            }

            firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .update(updates)
                .await()

            // Update cache
            orderDao.updateOrderStatus(orderId, status, System.currentTimeMillis())

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update order status: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun cancelOrder(orderId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch current order
            val document = firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .get()
                .await()

            val order = Order.fromFirestore(document)
                ?: throw Exception("Order not found")

            // Check if order can be cancelled
            if (!order.canCancel()) {
                throw Exception("This order cannot be cancelled")
            }

            // Create status update
            val statusUpdate = OrderStatusUpdate(
                status = "Cancelled",
                timestamp = Timestamp.now(),
                note = "Cancelled by user"
            )

            // Update order
            val updates = mapOf(
                "orderStatus" to "Cancelled",
                "paymentStatus" to "Refunded",
                "updatedAt" to Timestamp.now(),
                "statusHistory" to (order.statusHistory + statusUpdate).map { it.toMap() }
            )

            firestore.collection(FirestoreCollections.ORDERS)
                .document(orderId)
                .update(updates)
                .await()

            // Update cache
            orderDao.updateOrderStatus(orderId, "Cancelled", System.currentTimeMillis())

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to cancel order: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun syncOrders(userId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch all orders from Firestore
            val snapshot = firestore.collection(FirestoreCollections.ORDERS)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val orders = snapshot.documents.mapNotNull { Order.fromFirestore(it) }

            // Clear and update cache
            orderDao.clearUserOrders(userId)
            val entities = orders.map { OrderEntity.fromOrder(it) }
            orderDao.insertOrders(entities)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to sync orders: ${e.message}")))
        }
    }.flowOn(ioDispatcher)
}
