package com.arshop.util

/**
 * Application-wide constants.
 */
object Constants {
    
    // Firebase Collection Names
    object Collections {
        const val USERS = "users"
        const val PRODUCTS = "products"
        const val ORDERS = "orders"
        const val CART = "cart"
        const val ADMIN_USERS = "admin_users"
        const val REVIEWS = "reviews"
        const val CATEGORIES = "categories"
    }
    
    // Shared Preferences Keys
    object PrefsKeys {
        const val USER_ID = "user_id"
        const val IS_LOGGED_IN = "is_logged_in"
        const val THEME_MODE = "theme_mode"
        const val SHOW_AR_TUTORIAL = "show_ar_tutorial"
        const val LAST_SYNC_TIME = "last_sync_time"
        const val CART_COUNT = "cart_count"
    }
    
    // Request Codes
    object RequestCodes {
        const val CAMERA_PERMISSION = 1001
        const val STORAGE_PERMISSION = 1002
        const val LOCATION_PERMISSION = 1003
        const val AR_CAMERA = 1004
        const val PICK_IMAGE = 1005
    }
    
    // Date Formats
    object DateFormats {
        const val DISPLAY_DATE = "MMM dd, yyyy"
        const val DISPLAY_DATE_TIME = "MMM dd, yyyy hh:mm a"
        const val ORDER_DATE = "dd/MM/yyyy"
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val TIME_ONLY = "hh:mm a"
    }
    
    // File Size Limits
    object FileSizes {
        const val MAX_IMAGE_SIZE_MB = 5L
        const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024
        const val MAX_MODEL_SIZE_MB = 50L
        const val MAX_MODEL_SIZE_BYTES = MAX_MODEL_SIZE_MB * 1024 * 1024
    }
    
    // AR Configuration
    object AR {
        const val MIN_OPENGL_VERSION = 3.0
        const val PLANE_DISCOVERY_TIMEOUT = 30000L // 30 seconds
        const val MAX_TRACKING_IMAGES = 20
        const val MODEL_CACHE_SIZE = 100 * 1024 * 1024 // 100 MB
        const val DEFAULT_MODEL_SCALE = 1.0f
    }
    
    // Product Constants
    object Product {
        const val MIN_PRICE = 0.0
        const val MAX_PRICE = 10000.0
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_IMAGES_PER_PRODUCT = 5
    }
    
    // Cart Constants
    object Cart {
        const val MIN_QUANTITY = 1
        const val MAX_QUANTITY = 10
        const val FREE_SHIPPING_THRESHOLD = 50.0
        const val STANDARD_SHIPPING_COST = 5.99
        const val TAX_RATE = 0.08 // 8%
    }
    
    // Order Status
    object OrderStatus {
        const val PENDING = "Pending"
        const val CONFIRMED = "Confirmed"
        const val PROCESSING = "Processing"
        const val SHIPPED = "Shipped"
        const val DELIVERED = "Delivered"
        const val CANCELLED = "Cancelled"
        const val REFUNDED = "Refunded"
    }
    
    // Payment Methods
    object PaymentMethods {
        const val STRIPE = "Stripe"
        const val PAYPAL = "PayPal"
        const val RAZORPAY = "Razorpay"
        const val GOOGLE_PAY = "Google Pay"
    }
    
    // Network
    object Network {
        const val TIMEOUT_SECONDS = 30L
        const val RETRY_COUNT = 3
    }
    
    // Validation
    object Validation {
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 50
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
        const val PHONE_NUMBER_LENGTH = 10
        const val ZIP_CODE_LENGTH = 5
    }
    
    // Storage Paths
    object StoragePaths {
        const val PRODUCTS = "products"
        const val MODELS_3D = "models_3d"
        const val USER_AVATARS = "user_avatars"
        const val AR_SCREENSHOTS = "ar_screenshots"
    }
}
