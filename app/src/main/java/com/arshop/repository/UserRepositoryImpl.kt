package com.arshop.repository

import com.arshop.data.local.dao.UserDao
import com.arshop.data.local.entity.UserEntity
import com.arshop.data.model.Address
import com.arshop.data.model.User
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository.
 * Provides user profile management with Firestore and offline caching.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    private val gson = Gson()

    override fun getUserProfile(userId: String): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // First check cache
            val cachedUser = userDao.getUserByIdSync(userId)
            if (cachedUser != null) {
                emit(Result.Success(cachedUser.toUser()))
            }

            // Fetch from Firestore
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("User not found")

            // Update cache
            userDao.insertUser(UserEntity.fromUser(user))

            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load user profile: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateUserProfile(
        userId: String,
        displayName: String?,
        photoUrl: String?,
        phoneNumber: String?
    ): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // Build update map
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to Timestamp.now()
            )

            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoUrl"] = it }
            phoneNumber?.let { updates["phoneNumber"] = it }

            // Update Firestore
            firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .update(updates)
                .await()

            // Fetch updated user
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("Failed to load updated user")

            // Update cache
            userDao.insertUser(UserEntity.fromUser(user))

            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update profile: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateAddress(userId: String, address: Address): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Validate address
            if (!address.isValid()) {
                throw Exception("Invalid address information")
            }

            // Fetch current user
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("User not found")

            // Update addresses list
            val addresses = user.addresses.toMutableList()
            val existingIndex = addresses.indexOfFirst { it.id == address.id }

            if (existingIndex >= 0) {
                addresses[existingIndex] = address
            } else {
                addresses.add(address)
            }

            // If this is set as default, unset others
            if (address.isDefault) {
                addresses.replaceAll {
                    if (it.id == address.id) address else it.copy(isDefault = false)
                }
            }

            // Update Firestore
            firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .update(
                    mapOf(
                        "addresses" to addresses.map { it.toMap() },
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            // Update cache
            val gson = Gson()
            userDao.updateAddresses(userId, gson.toJson(addresses), System.currentTimeMillis())

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to update address: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun removeAddress(userId: String, addressId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch current user
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("User not found")

            // Remove address from list
            val addresses = user.addresses.filter { it.id != addressId }

            // Update Firestore
            firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .update(
                    mapOf(
                        "addresses" to addresses.map { it.toMap() },
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            // Update cache
            userDao.updateAddresses(userId, gson.toJson(addresses), System.currentTimeMillis())

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to remove address: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getAddresses(userId: String): Flow<Result<List<Address>>> = flow {
        try {
            emit(Result.Loading)

            // Fetch user profile which includes addresses
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("User not found")

            emit(Result.Success(user.addresses))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to load addresses: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun setDefaultAddress(userId: String, addressId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            // Fetch current user
            val document = firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .get()
                .await()

            val user = User.fromFirestore(document)
                ?: throw Exception("User not found")

            // Update addresses - set new default and unset others
            val addresses = user.addresses.map { address ->
                address.copy(isDefault = address.id == addressId)
            }

            // Update Firestore
            firestore.collection(FirestoreCollections.USERS)
                .document(userId)
                .update(
                    mapOf(
                        "addresses" to addresses.map { it.toMap() },
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            // Update cache
            userDao.updateAddresses(userId, gson.toJson(addresses), System.currentTimeMillis())

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to set default address: ${e.message}")))
        }
    }.flowOn(ioDispatcher)
}
