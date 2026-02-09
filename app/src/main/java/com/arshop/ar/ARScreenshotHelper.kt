package com.arshop.ar

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for capturing AR screenshots and saving to gallery.
 * Handles permissions, saving to MediaStore, and sharing functionality.
 */
class ARScreenshotHelper(private val context: Context) {
    
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    /**
     * Captures and saves AR view as image to gallery.
     * @param bitmap The AR view bitmap to save
     * @return Uri of saved image, or null if failed
     */
    suspend fun saveToGallery(bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        try {
            val filename = "ARShop_${dateFormat.format(Date())}.jpg"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ using MediaStore
                saveToMediaStore(bitmap, filename)
            } else {
                // Android 9 and below
                saveToExternalStorage(bitmap, filename)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Saves bitmap to MediaStore (Android 10+).
     */
    private suspend fun saveToMediaStore(bitmap: Bitmap, filename: String): Uri? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null
        }
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ARShop")
        }
        
        val resolver = context.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        return imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }
            uri
        }
    }
    
    /**
     * Saves bitmap to external storage (Android 9 and below).
     */
    private suspend fun saveToExternalStorage(bitmap: Bitmap, filename: String): Uri? {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val arshopDir = File(picturesDir, "ARShop")
        
        if (!arshopDir.exists()) {
            arshopDir.mkdirs()
        }
        
        val imageFile = File(arshopDir, filename)
        
        return try {
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }
            
            // Notify gallery of new image using MediaScannerConnection
            MediaScannerConnection.scanFile(
                context,
                arrayOf(imageFile.absolutePath),
                arrayOf("image/jpeg"),
                null
            )
            
            Uri.fromFile(imageFile)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Creates a share intent for the saved image.
     * @param imageUri Uri of the saved image
     * @return Share intent
     */
    fun createShareIntent(imageUri: Uri): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "Check out this AR try-on from ARShop!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        return Intent.createChooser(shareIntent, "Share AR Try-On")
    }
    
    /**
     * Saves bitmap to temporary cache for sharing.
     * @param bitmap The bitmap to save
     * @return Uri of cached file
     */
    suspend fun saveToCacheForSharing(bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            
            val filename = "ar_share_${dateFormat.format(Date())}.jpg"
            val file = File(cachePath, filename)
            
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            
            // Get content URI using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Clears cached screenshots to free space.
     */
    fun clearCache() {
        val cachePath = File(context.cacheDir, "images")
        cachePath.listFiles()?.forEach { it.delete() }
    }
    
    /**
     * Gets cache size in bytes.
     */
    fun getCacheSize(): Long {
        val cachePath = File(context.cacheDir, "images")
        return cachePath.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    /**
     * Saves bitmap with custom quality.
     * @param bitmap Bitmap to save
     * @param quality JPEG quality (0-100)
     * @return Uri of saved image
     */
    suspend fun saveWithQuality(bitmap: Bitmap, quality: Int): Uri? = withContext(Dispatchers.IO) {
        try {
            val filename = "ARShop_${dateFormat.format(Date())}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, 
                        Environment.DIRECTORY_PICTURES + "/ARShop")
                }
            }
            
            val resolver = context.contentResolver
            val imageUri = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
                contentValues
            )
            
            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                }
                uri
            }
        } catch (e: Exception) {
            null
        }
    }
}
