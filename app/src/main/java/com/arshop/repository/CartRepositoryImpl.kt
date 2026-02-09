package com.arshop.repository

import com.arshop.data.local.dao.CartDao
import com.arshop.data.local.entity.CartItemEntity
import com.arshop.data.model.CartItem
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CartRepository.
 * Provides real-time cart synchronization with Firestore and local caching.
 */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cartDao: CartDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CartRepository {

    override fun getCartItems(userId: String): Flow<Result<List<CartItem>>> = callbackFlow {
        try {
            trySend(Result.Loading)

            // Listen to Firestore for real-time updates
            val listener = firestore.collection(FirestoreCollections.CART_ITEMS)
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.Failure(Exception("Failed to load cart: ${error.message}")))
                        return@addSnapshotListener
                    }

                    val cartItems = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            CartItem(
                                id = doc.id,
                                productId = doc.getString("productId") ?: "",
                                userId = doc.getString("userId") ?: "",
                                productName = doc.getString("productName") ?: "",
                                productImage = doc.getString("productImage") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                size = doc.getString("size") ?: "",
                                color = doc.getString("color") ?: "",
                                quantity = doc.getLong("quantity")?.toInt() ?: 1,
                                addedAt = doc.getTimestamp("addedAt") ?: com.google.firebase.Timestamp.now()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    // Cache locally using launch
                    kotlinx.coroutines.CoroutineScope(ioDispatcher).launch {
                        val entities = cartItems.map { CartItemEntity.fromCartItem(it) }
                        cartDao.insertCartItems(entities)
                    }

                    trySend(Result.Success(cartItems))
                }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            trySend(Result.Failure(Exception("Failed to load cart: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun addToCart(cartItem: CartItem): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Add to Firestore
            val cartItemMap = mapOf(
                "productId" to cartItem.productId,
                "userId" to cartItem.userId,
                "productName" to cartItem.productName,
                "productImage" to cartItem.productImage,
                "price" to cartItem.price,
                "size" to cartItem.size,
                "color" to cartItem.color,
                "quantity" to cartItem.quantity,
                "addedAt" to cartItem.addedAt
            )

            if (cartItem.id.isNotEmpty()) {
                firestore.collection(FirestoreCollections.CART_ITEMS)
                    .document(cartItem.id)
                    .set(cartItemMap)
                    .await()
            } else {
                firestore.collection(FirestoreCollections.CART_ITEMS)
                    .add(cartItemMap)
                    .await()
            }

            // Cache locally
            cartDao.insertCartItem(CartItemEntity.fromCartItem(cartItem))

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to add to cart: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateQuantity(cartItemId: String, quantity: Int): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            if (quantity <= 0) {
                throw Exception("Quantity must be greater than 0")
            }

            // Update in Firestore
            firestore.collection(FirestoreCollections.CART_ITEMS)
                .document(cartItemId)
                .update("quantity", quantity)
                .await()

            // Update cache
            cartDao.updateQuantity(cartItemId, quantity)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update quantity: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun removeFromCart(cartItemId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Remove from Firestore
            firestore.collection(FirestoreCollections.CART_ITEMS)
                .document(cartItemId)
                .delete()
                .await()

            // Remove from cache
            cartDao.deleteCartItem(cartItemId)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to remove from cart: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun clearCart(userId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Get all cart items for the user
            val snapshot = firestore.collection(FirestoreCollections.CART_ITEMS)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Delete all items in a batch
            val batch = firestore.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            // Clear local cache
            cartDao.clearCart(userId)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to clear cart: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun syncCart(userId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch all cart items from Firestore
            val snapshot = firestore.collection(FirestoreCollections.CART_ITEMS)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val cartItems = snapshot.documents.mapNotNull { doc ->
                try {
                    CartItem(
                        id = doc.id,
                        productId = doc.getString("productId") ?: "",
                        userId = doc.getString("userId") ?: "",
                        productName = doc.getString("productName") ?: "",
                        productImage = doc.getString("productImage") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        size = doc.getString("size") ?: "",
                        color = doc.getString("color") ?: "",
                        quantity = doc.getLong("quantity")?.toInt() ?: 1,
                        addedAt = doc.getTimestamp("addedAt") ?: com.google.firebase.Timestamp.now()
                    )
                } catch (e: Exception) {
                    null
                }
            }

            // Clear and update local cache
            cartDao.clearCart(userId)
            val entities = cartItems.map { CartItemEntity.fromCartItem(it) }
            cartDao.insertCartItems(entities)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to sync cart: ${e.message}")))
        }
    }.flowOn(ioDispatcher)
}
