package com.arshop.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility object for runtime permission handling.
 */
object PermissionUtils {
    
    /**
     * Checks if a specific permission is granted.
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Checks if camera permission is granted.
     */
    fun isCameraPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.CAMERA)
    }
    
    /**
     * Checks if storage permission is granted.
     */
    fun isStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    
    /**
     * Checks if write storage permission is granted.
     */
    fun isWriteStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Scoped storage, no permission needed
        } else {
            isPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    
    /**
     * Checks if location permission is granted.
     */
    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
               isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    
    /**
     * Requests camera permission.
     */
    fun requestCameraPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            Constants.RequestCodes.CAMERA_PERMISSION
        )
    }
    
    /**
     * Requests storage permission.
     */
    fun requestStoragePermission(activity: Activity) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            Constants.RequestCodes.STORAGE_PERMISSION
        )
    }
    
    /**
     * Requests location permission.
     */
    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.RequestCodes.LOCATION_PERMISSION
        )
    }
    
    /**
     * Checks if permission rationale should be shown.
     */
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        permission: String
    ): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
    
    /**
     * Checks if all permissions in the array are granted.
     */
    fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }
    
    /**
     * Gets required permissions for AR features.
     */
    fun getArPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.CAMERA
        )
    }
    
    /**
     * Checks if all AR permissions are granted.
     */
    fun areArPermissionsGranted(context: Context): Boolean {
        return arePermissionsGranted(context, getArPermissions())
    }
    
    /**
     * Requests all AR permissions.
     */
    fun requestArPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            getArPermissions(),
            Constants.RequestCodes.AR_CAMERA
        )
    }
    
    /**
     * Handles permission request result.
     */
    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (grantResults.isNotEmpty() && 
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            onGranted()
        } else {
            onDenied()
        }
    }
}

/**
 * Context extension to check permission.
 */
fun Context.hasPermission(permission: String): Boolean {
    return PermissionUtils.isPermissionGranted(this, permission)
}

/**
 * Context extension to check camera permission.
 */
fun Context.hasCameraPermission(): Boolean {
    return PermissionUtils.isCameraPermissionGranted(this)
}

/**
 * Context extension to check storage permission.
 */
fun Context.hasStoragePermission(): Boolean {
    return PermissionUtils.isStoragePermissionGranted(this)
}
