package com.arshop.ui.navigation

import androidx.navigation.NavHostController

/**
 * Navigation actions helper class for ARShop application
 *
 * Provides type-safe navigation methods for all app destinations
 *
 * @param navController The NavHostController instance
 */
class NavigationActions(private val navController: NavHostController) {
    
    // Authentication Navigation
    fun navigateToLogin() {
        navController.navigate(Destinations.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }
    
    fun navigateToSignUp() {
        navController.navigate(Destinations.SignUp.route)
    }
    
    fun navigateToForgotPassword() {
        navController.navigate(Destinations.ForgotPassword.route)
    }
    
    // Main App Navigation
    fun navigateToHome() {
        navController.navigate(Destinations.Home.route) {
            popUpTo(Destinations.Home.route) { inclusive = true }
        }
    }
    
    fun navigateToBrowse() {
        navController.navigate(Destinations.Browse.route)
    }
    
    fun navigateToSearch() {
        navController.navigate(Destinations.Search.route)
    }
    
    // Product Navigation
    fun navigateToProductDetail(productId: String) {
        navController.navigate(Destinations.ProductDetail.createRoute(productId))
    }
    
    // Cart & Checkout Navigation
    fun navigateToCart() {
        navController.navigate(Destinations.Cart.route)
    }
    
    fun navigateToCheckout() {
        navController.navigate(Destinations.Checkout.route)
    }
    
    fun navigateToPayment() {
        navController.navigate(Destinations.Payment.route)
    }
    
    fun navigateToOrderConfirmation(orderId: String) {
        navController.navigate(Destinations.OrderConfirmation.createRoute(orderId)) {
            // Clear checkout flow from back stack
            popUpTo(Destinations.Home.route) { inclusive = false }
        }
    }
    
    // User Profile Navigation
    fun navigateToProfile() {
        navController.navigate(Destinations.Profile.route)
    }
    
    fun navigateToOrderHistory() {
        navController.navigate(Destinations.OrderHistory.route)
    }
    
    fun navigateToOrderDetail(orderId: String) {
        navController.navigate(Destinations.OrderDetail.createRoute(orderId))
    }
    
    // AR Navigation
    fun navigateToARTryOn(productId: String) {
        navController.navigate(Destinations.ARTryOn.createRoute(productId))
    }
    
    // Admin Navigation
    fun navigateToAdminDashboard() {
        navController.navigate(Destinations.AdminDashboard.route)
    }
    
    fun navigateToAdminProducts() {
        navController.navigate(Destinations.AdminProducts.route)
    }
    
    fun navigateToAdminOrders() {
        navController.navigate(Destinations.AdminOrders.route)
    }
    
    fun navigateToAdminProductEdit(productId: String) {
        navController.navigate(Destinations.AdminProductEdit.createRoute(productId))
    }
    
    fun navigateToAdminProductNew() {
        navController.navigate(Destinations.AdminProductEdit.createRouteNew())
    }
    
    fun navigateToAdminOrderDetail(orderId: String) {
        navController.navigate(Destinations.AdminOrderDetail.createRoute(orderId))
    }
    
    // Back Navigation
    fun navigateBack() {
        navController.popBackStack()
    }
    
    fun navigateBackWithResult() {
        navController.popBackStack()
    }
    
    // Clear Back Stack Helpers
    fun clearBackStackAndNavigateToHome() {
        navController.navigate(Destinations.Home.route) {
            popUpTo(0) { inclusive = true }
        }
    }
    
    fun clearBackStackAndNavigateToLogin() {
        navController.navigate(Destinations.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }
    
    fun clearCheckoutStackAndNavigateToHome() {
        navController.navigate(Destinations.Home.route) {
            popUpTo(Destinations.Home.route) { inclusive = true }
        }
    }
}
