package com.arshop.ui.navigation

/**
 * Navigation destinations for ARShop application
 */
sealed class Destinations(val route: String) {
    
    // Authentication
    object Login : Destinations("login")
    object SignUp : Destinations("signup")
    object ForgotPassword : Destinations("forgot_password")
    
    // Main App
    object Home : Destinations("home")
    object Browse : Destinations("browse")
    object Search : Destinations("search")
    
    // Product
    object ProductDetail : Destinations("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
        const val ARG_PRODUCT_ID = "productId"
    }
    
    // Cart & Checkout
    object Cart : Destinations("cart")
    object Checkout : Destinations("checkout")
    object Payment : Destinations("payment")
    object OrderConfirmation : Destinations("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/$orderId"
        const val ARG_ORDER_ID = "orderId"
    }
    
    // User Profile
    object Profile : Destinations("profile")
    object OrderHistory : Destinations("order_history")
    object OrderDetail : Destinations("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
        const val ARG_ORDER_ID = "orderId"
    }
    
    // AR Features
    object ARTryOn : Destinations("ar_tryon/{productId}") {
        fun createRoute(productId: String) = "ar_tryon/$productId"
        const val ARG_PRODUCT_ID = "productId"
    }
    
    // Admin
    object AdminDashboard : Destinations("admin/dashboard")
    object AdminProducts : Destinations("admin/products")
    object AdminOrders : Destinations("admin/orders")
    object AdminProductEdit : Destinations("admin/product/{productId}") {
        fun createRoute(productId: String) = "admin/product/$productId"
        fun createRouteNew() = "admin/product/new"
        const val ARG_PRODUCT_ID = "productId"
    }
    object AdminOrderDetail : Destinations("admin/order/{orderId}") {
        fun createRoute(orderId: String) = "admin/order/$orderId"
        const val ARG_ORDER_ID = "orderId"
    }
}

/**
 * Routes that should show the bottom navigation bar
 */
val bottomNavRoutes = setOf(
    Destinations.Home.route,
    Destinations.Browse.route,
    Destinations.Cart.route,
    Destinations.Profile.route
)

/**
 * Routes that require authentication
 */
val authRequiredRoutes = setOf(
    Destinations.Cart.route,
    Destinations.Checkout.route,
    Destinations.Payment.route,
    Destinations.Profile.route,
    Destinations.OrderHistory.route
)

/**
 * Admin routes that require admin role
 */
val adminRoutes = setOf(
    Destinations.AdminDashboard.route,
    Destinations.AdminProducts.route,
    Destinations.AdminOrders.route
)
