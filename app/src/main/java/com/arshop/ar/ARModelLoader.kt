package com.arshop.ar

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Loads 3D models from Firebase Storage and caches them locally.
 * Supports GLB and GLTF formats for AR rendering.
 */
class ARModelLoader(
    private val context: Context,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val modelCache = mutableMapOf<String, File>()
    
    /**
     * Downloads 3D model from Firebase Storage URL.
     * @param modelUrl Firebase Storage URL (gs:// or https://)
     * @return Local file containing the model
     */
    suspend fun downloadModel(modelUrl: String): File = suspendCancellableCoroutine { continuation ->
        _isLoading.value = true
        _loadingProgress.value = 0f
        _error.value = null
        
        try {
            // Check cache first
            modelCache[modelUrl]?.let { cachedFile ->
                if (cachedFile.exists()) {
                    _isLoading.value = false
                    _loadingProgress.value = 1f
                    continuation.resume(cachedFile)
                    return@suspendCancellableCoroutine
                }
            }
            
            // Parse Firebase Storage reference
            val storageRef = if (modelUrl.startsWith("gs://")) {
                storage.getReferenceFromUrl(modelUrl)
            } else if (modelUrl.startsWith("https://")) {
                storage.getReferenceFromUrl(modelUrl)
            } else {
                throw IllegalArgumentException("Invalid model URL format")
            }
            
            // Create local cache file
            val fileName = storageRef.name
            val localFile = File(context.cacheDir, "ar_models/$fileName")
            localFile.parentFile?.mkdirs()
            
            // Download with progress tracking
            val downloadTask = storageRef.getFile(localFile)
            
            downloadTask.addOnProgressListener { taskSnapshot ->
                val progress = taskSnapshot.bytesTransferred.toFloat() / 
                              taskSnapshot.totalByteCount.toFloat()
                _loadingProgress.value = progress
            }
            
            downloadTask.addOnSuccessListener {
                modelCache[modelUrl] = localFile
                _isLoading.value = false
                _loadingProgress.value = 1f
                _error.value = null
                continuation.resume(localFile)
            }
            
            downloadTask.addOnFailureListener { exception ->
                _isLoading.value = false
                _error.value = "Failed to download model: ${exception.message}"
                localFile.delete()
                continuation.resumeWithException(exception)
            }
            
            continuation.invokeOnCancellation {
                downloadTask.cancel()
                _isLoading.value = false
            }
            
        } catch (e: Exception) {
            _isLoading.value = false
            _error.value = "Failed to load model: ${e.message}"
            continuation.resumeWithException(e)
        }
    }
    
    /**
     * Creates a ModelNode from a local model file.
     * @param modelFile Local GLB/GLTF file
     * @return ModelNode ready for AR scene
     * 
     * TODO: Implement actual ModelNode creation using Sceneview
     * This is a placeholder implementation
     */
    fun createModelNode(modelFile: File): ModelNode? {
        return try {
            // TODO: Load GLB/GLTF using Sceneview
            // val modelNode = ModelNode()
            // modelNode.loadModel(context, modelFile.path)
            null
        } catch (e: Exception) {
            _error.value = "Failed to create model node: ${e.message}"
            null
        }
    }
    
    /**
     * Loads model from URL and creates ModelNode.
     * @param modelUrl Firebase Storage URL
     * @return ModelNode ready for rendering
     */
    suspend fun loadModelNode(modelUrl: String): ModelNode? {
        return try {
            val modelFile = downloadModel(modelUrl)
            createModelNode(modelFile)
        } catch (e: Exception) {
            _error.value = "Failed to load model: ${e.message}"
            null
        }
    }
    
    /**
     * Checks if a model is cached locally.
     */
    fun isModelCached(modelUrl: String): Boolean {
        return modelCache[modelUrl]?.exists() == true
    }
    
    /**
     * Gets cached model file if available.
     */
    fun getCachedModel(modelUrl: String): File? {
        return modelCache[modelUrl]?.takeIf { it.exists() }
    }
    
    /**
     * Clears model cache to free up space.
     */
    fun clearCache() {
        modelCache.values.forEach { it.delete() }
        modelCache.clear()
        
        // Clear cache directory
        File(context.cacheDir, "ar_models").listFiles()?.forEach { it.delete() }
    }
    
    /**
     * Gets cache size in bytes.
     */
    fun getCacheSize(): Long {
        val cachePath = File(context.cacheDir, "ar_models")
        if (!cachePath.exists() || !cachePath.isDirectory) {
            return 0L
        }
        return cachePath.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    /**
     * Validates if file is a valid 3D model format.
     */
    private fun isValidModelFormat(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension in listOf("glb", "gltf")
    }
    
    /**
     * Clears error state.
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Resets loader state.
     */
    fun reset() {
        _isLoading.value = false
        _loadingProgress.value = 0f
        _error.value = null
    }
}
