package com.arshop.repository

import android.net.Uri
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Firebase Storage operations.
 * Handles file uploads and downloads for products, AR models, and user content.
 */
interface StorageRepository {
    
    /**
     * Upload a product image.
     * @return Download URL of the uploaded image
     */
    fun uploadProductImage(imageUri: Uri, productId: String): Flow<Result<String>>
    
    /**
     * Upload a 3D model file.
     * @return Download URL of the uploaded model
     */
    fun upload3DModel(modelUri: Uri, productId: String): Flow<Result<String>>
    
    /**
     * Upload user profile photo.
     * @return Download URL of the uploaded photo
     */
    fun uploadProfilePhoto(photoUri: Uri, userId: String): Flow<Result<String>>
    
    /**
     * Get download URL for a file.
     */
    fun getDownloadUrl(path: String): Flow<Result<String>>
    
    /**
     * Delete a file from storage.
     */
    fun deleteFile(path: String): Flow<Result<Unit>>
    
    /**
     * Upload file with progress tracking.
     * @return Flow emitting upload progress (0-100) and final download URL
     */
    fun uploadWithProgress(fileUri: Uri, path: String): Flow<Result<UploadProgress>>
}

/**
 * Data class representing upload progress.
 */
data class UploadProgress(
    val progress: Int,
    val downloadUrl: String? = null,
    val isComplete: Boolean = false
)
