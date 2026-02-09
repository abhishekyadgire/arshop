package com.arshop.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * Main navigation graph for ARShop application
 *
 * @param navController The navigation controller
 * @param startDestination The starting destination (default: Home)
 * @param modifier Modifier for the NavHost
 */
@Composable
fun ARShopNavGraph(
    navController: NavHostController,
    startDestination: String = Destinations.Home.route,
    modifier: Modifier = Modifier
) {
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication Flow
        composable(Destinations.Login.route) {
            com.arshop.ui.screens.auth.LoginScreen(navigationActions)
        }
        
        composable(Destinations.SignUp.route) {
            com.arshop.ui.screens.auth.SignUpScreen(navigationActions)
        }
        
        composable(Destinations.ForgotPassword.route) {
            com.arshop.ui.screens.auth.ForgotPasswordScreen(navigationActions)
        }
        
        // Main App Flow
        composable(Destinations.Home.route) {
            com.arshop.ui.screens.home.HomeScreen(navigationActions)
        }
        
        composable(Destinations.Browse.route) {
            com.arshop.ui.screens.browse.BrowseScreen(navigationActions)
        }
        
        composable(Destinations.Search.route) {
            com.arshop.ui.screens.browse.SearchScreen(navigationActions)
        }
        
        // Product Detail with deep link support
        composable(
            route = Destinations.ProductDetail.route,
            arguments = listOf(
                navArgument(Destinations.ProductDetail.ARG_PRODUCT_ID) {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "arshop://product/{${Destinations.ProductDetail.ARG_PRODUCT_ID}}"
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Destinations.ProductDetail.ARG_PRODUCT_ID)
            com.arshop.ui.screens.product.ProductDetailScreen(productId, navigationActions)
        }
        
        // Cart & Checkout Flow
        composable(Destinations.Cart.route) {
            com.arshop.ui.screens.cart.CartScreen(navigationActions)
        }
        
        composable(Destinations.Checkout.route) {
            com.arshop.ui.screens.checkout.CheckoutScreen(navigationActions)
        }
        
        composable(Destinations.Payment.route) {
            com.arshop.ui.screens.checkout.PaymentScreen(navigationActions)
        }
        
        composable(
            route = Destinations.OrderConfirmation.route,
            arguments = listOf(
                navArgument(Destinations.OrderConfirmation.ARG_ORDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Destinations.OrderConfirmation.ARG_ORDER_ID)
            com.arshop.ui.screens.checkout.OrderConfirmationScreen(orderId, navigationActions)
        }
        
        // User Profile Flow
        composable(Destinations.Profile.route) {
            com.arshop.ui.screens.profile.ProfileScreen(navigationActions)
        }
        
        composable(Destinations.OrderHistory.route) {
            com.arshop.ui.screens.profile.OrderHistoryScreen(navigationActions)
        }
        
        composable(
            route = Destinations.OrderDetail.route,
            arguments = listOf(
                navArgument(Destinations.OrderDetail.ARG_ORDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Destinations.OrderDetail.ARG_ORDER_ID)
            com.arshop.ui.screens.profile.OrderDetailScreen(orderId, navigationActions)
        }
        
        // AR Features
        composable(
            route = Destinations.ARTryOn.route,
            arguments = listOf(
                navArgument(Destinations.ARTryOn.ARG_PRODUCT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Destinations.ARTryOn.ARG_PRODUCT_ID)
            // ARTryOnScreen(productId, navigationActions)
        }
        
        // Admin Flow
        composable(Destinations.AdminDashboard.route) {
            // AdminDashboardScreen(navigationActions)
        }
        
        composable(Destinations.AdminProducts.route) {
            // AdminProductsScreen(navigationActions)
        }
        
        composable(Destinations.AdminOrders.route) {
            // AdminOrdersScreen(navigationActions)
        }
        
        composable(
            route = Destinations.AdminProductEdit.route,
            arguments = listOf(
                navArgument(Destinations.AdminProductEdit.ARG_PRODUCT_ID) {
                    type = NavType.StringType
                    defaultValue = "new"
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(Destinations.AdminProductEdit.ARG_PRODUCT_ID)
            // AdminProductEditScreen(productId, navigationActions)
        }
        
        composable(
            route = Destinations.AdminOrderDetail.route,
            arguments = listOf(
                navArgument(Destinations.AdminOrderDetail.ARG_ORDER_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Destinations.AdminOrderDetail.ARG_ORDER_ID)
            // AdminOrderDetailScreen(orderId, navigationActions)
        }
    }
}
