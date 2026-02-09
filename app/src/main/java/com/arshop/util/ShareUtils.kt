package com.arshop.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Utility object for sharing functionality.
 */
object ShareUtils {
    
    /**
     * Shares a product link via system share sheet.
     */
    fun shareProduct(
        context: Context,
        productName: String,
        productUrl: String
    ) {
        val shareText = buildString {
            append("Check out this amazing product: $productName\n\n")
            append(productUrl)
        }
        
        shareText(context, shareText, "Share Product")
    }
    
    /**
     * Shares plain text via system share sheet.
     */
    fun shareText(
        context: Context,
        text: String,
        title: String = "Share"
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        
        val chooserIntent = Intent.createChooser(intent, title)
        context.startActivity(chooserIntent)
    }
    
    /**
     * Shares an image via system share sheet.
     */
    fun shareImage(
        context: Context,
        imageUri: Uri,
        title: String = "Share Image"
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooserIntent = Intent.createChooser(intent, title)
        context.startActivity(chooserIntent)
    }
    
    /**
     * Shares a bitmap as an image.
     */
    fun shareBitmap(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "shared_image.jpg",
        title: String = "Share Image"
    ) {
        val uri = saveBitmapToCache(context, bitmap, fileName)
        uri?.let { shareImage(context, it, title) }
    }
    
    /**
     * Shares an AR screenshot.
     */
    fun shareArScreenshot(
        context: Context,
        bitmap: Bitmap,
        productName: String
    ) {
        val fileName = "ar_screenshot_${System.currentTimeMillis()}.jpg"
        val shareText = "Check out how $productName looks in AR!"
        
        val uri = saveBitmapToCache(context, bitmap, fileName)
        uri?.let { 
            shareImageWithText(context, it, shareText, "Share AR Experience")
        }
    }
    
    /**
     * Shares image with accompanying text.
     */
    fun shareImageWithText(
        context: Context,
        imageUri: Uri,
        text: String,
        title: String = "Share"
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooserIntent = Intent.createChooser(intent, title)
        context.startActivity(chooserIntent)
    }
    
    /**
     * Saves bitmap to cache directory and returns URI.
     */
    private fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            
            val file = File(cachePath, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
            }
            
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
     * Shares order details.
     */
    fun shareOrderDetails(
        context: Context,
        orderId: String,
        orderTotal: String,
        itemCount: Int
    ) {
        val shareText = buildString {
            append("Order Confirmation\n\n")
            append("Order ID: $orderId\n")
            append("Items: $itemCount\n")
            append("Total: $orderTotal\n\n")
            append("Thank you for shopping with ARShop!")
        }
        
        shareText(context, shareText, "Share Order")
    }
    
    /**
     * Creates a share intent for app link.
     */
    fun shareAppLink(context: Context) {
        val appLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val shareText = buildString {
            append("Check out ARShop - Shop with Augmented Reality!\n\n")
            append(appLink)
        }
        
        shareText(context, shareText, "Share ARShop")
    }
    
    /**
     * Creates feedback email intent.
     */
    fun sendFeedbackEmail(
        context: Context,
        recipientEmail: String = "support@arshop.com",
        subject: String = "ARShop Feedback"
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    
    /**
     * Opens URL in browser.
     */
    fun openUrlInBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    
    /**
     * Shares multiple images.
     */
    fun shareMultipleImages(
        context: Context,
        imageUris: ArrayList<Uri>,
        title: String = "Share Images"
    ) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooserIntent = Intent.createChooser(intent, title)
        context.startActivity(chooserIntent)
    }
}
