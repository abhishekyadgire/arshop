package com.arshop.repository

import android.net.Uri
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.storage.FirebaseStorage
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
 * Implementation of StorageRepository.
 * Handles Firebase Storage operations for file uploads and downloads.
 */
@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : StorageRepository {

    companion object {
        private const val PRODUCTS_PATH = "products"
        private const val MODELS_PATH = "models"
        private const val PROFILE_PHOTOS_PATH = "profile_photos"
    }

    override fun uploadProductImage(imageUri: Uri, productId: String): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)

            val fileName = "${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference
                .child(PRODUCTS_PATH)
                .child(productId)
                .child(fileName)

            // Upload file
            storageRef.putFile(imageUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            emit(Result.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to upload image: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun upload3DModel(modelUri: Uri, productId: String): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)

            val fileName = "${productId}_model.glb"
            val storageRef = storage.reference
                .child(MODELS_PATH)
                .child(productId)
                .child(fileName)

            // Upload file
            storageRef.putFile(modelUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            emit(Result.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to upload 3D model: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun uploadProfilePhoto(photoUri: Uri, userId: String): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)

            val fileName = "${userId}_profile.jpg"
            val storageRef = storage.reference
                .child(PROFILE_PHOTOS_PATH)
                .child(fileName)

            // Upload file
            storageRef.putFile(photoUri).await()

            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()

            emit(Result.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to upload profile photo: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun getDownloadUrl(path: String): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)

            val storageRef = storage.reference.child(path)
            val downloadUrl = storageRef.downloadUrl.await().toString()

            emit(Result.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to get download URL: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun deleteFile(path: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            val storageRef = storage.reference.child(path)
            storageRef.delete().await()

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to delete file: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun uploadWithProgress(fileUri: Uri, path: String): Flow<Result<UploadProgress>> = callbackFlow {
        try {
            trySend(Result.Loading)

            val storageRef = storage.reference.child(path)
            val uploadTask = storageRef.putFile(fileUri)

            // Listen to upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                trySend(Result.Success(UploadProgress(progress = progress)))
            }.addOnSuccessListener {
                // Get download URL on success
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    trySend(
                        Result.Success(
                            UploadProgress(
                                progress = 100,
                                downloadUrl = uri.toString(),
                                isComplete = true
                            )
                        )
                    )
                    close()
                }.addOnFailureListener { e ->
                    trySend(Result.Failure(Exception("Failed to get download URL: ${e.message}")))
                    close()
                }
            }.addOnFailureListener { e ->
                trySend(Result.Failure(Exception("Upload failed: ${e.message}")))
                close()
            }

            awaitClose { uploadTask.cancel() }
        } catch (e: Exception) {
            trySend(Result.Failure(Exception("Upload failed: ${e.message}")))
            close()
        }
    }.flowOn(ioDispatcher)
}
