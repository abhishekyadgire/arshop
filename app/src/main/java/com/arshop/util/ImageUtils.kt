package com.arshop.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Utility object for image processing operations.
 */
object ImageUtils {
    
    /**
     * Compresses an image to reduce file size.
     */
    fun compressImage(
        bitmap: Bitmap,
        quality: Int = 80,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024
    ): Bitmap {
        val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)
        return scaledBitmap
    }
    
    /**
     * Scales a bitmap to fit within max dimensions while maintaining aspect ratio.
     */
    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        val scale = minOf(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )
        
        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
    }
    
    /**
     * Converts bitmap to byte array.
     */
    fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 80
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, outputStream)
        return outputStream.toByteArray()
    }
    
    /**
     * Converts byte array to bitmap.
     */
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Loads bitmap from URI with optional scaling.
     */
    fun loadBitmapFromUri(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024
    ): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let { scaleBitmap(it, maxWidth, maxHeight) }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Fixes image orientation based on EXIF data.
     */
    fun fixImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }
            
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: IOException) {
            bitmap
        }
    }
    
    /**
     * Saves bitmap to file.
     */
    fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90
    ): Boolean {
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(format, quality, outputStream)
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Converts file to byte array.
     */
    fun fileToByteArray(file: File): ByteArray? {
        return try {
            file.readBytes()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Converts URI to byte array.
     */
    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Gets file size from URI.
     */
    fun getFileSizeFromUri(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                pfd.statSize
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Checks if file size is within limit.
     */
    fun isFileSizeValid(context: Context, uri: Uri, maxSizeBytes: Long): Boolean {
        val fileSize = getFileSizeFromUri(context, uri)
        return fileSize > 0 && fileSize <= maxSizeBytes
    }
    
    /**
     * Checks if image file size is valid for upload.
     */
    fun isImageSizeValid(context: Context, uri: Uri): Boolean {
        return isFileSizeValid(context, uri, Constants.FileSizes.MAX_IMAGE_SIZE_BYTES)
    }
    
    /**
     * Creates a thumbnail from bitmap.
     */
    fun createThumbnail(bitmap: Bitmap, size: Int = 200): Bitmap {
        return scaleBitmap(bitmap, size, size)
    }
    
    /**
     * Calculates sample size for efficient bitmap loading.
     */
    fun calculateSampleSize(
        inputStream: InputStream,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        
        val height = options.outHeight
        val width = options.outWidth
        var sampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / sampleSize >= reqHeight && 
                   halfWidth / sampleSize >= reqWidth) {
                sampleSize *= 2
            }
        }
        
        return sampleSize
    }
    
    /**
     * Decodes bitmap with sample size for memory efficiency.
     */
    fun decodeSampledBitmap(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return try {
            var sampleSize = 1
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                sampleSize = calculateSampleSize(inputStream, reqWidth, reqHeight)
            }
            
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            null
        }
    }
}
