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
            // LoginScreen(navigationActions)
            // Placeholder - actual screen will be implemented later
        }
        
        composable(Destinations.SignUp.route) {
            // SignUpScreen(navigationActions)
        }
        
        composable(Destinations.ForgotPassword.route) {
            // ForgotPasswordScreen(navigationActions)
        }
        
        // Main App Flow
        composable(Destinations.Home.route) {
            // HomeScreen(navigationActions)
        }
        
        composable(Destinations.Browse.route) {
            // BrowseScreen(navigationActions)
        }
        
        composable(Destinations.Search.route) {
            // SearchScreen(navigationActions)
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
            // ProductDetailScreen(productId, navigationActions)
        }
        
        // Cart & Checkout Flow
        composable(Destinations.Cart.route) {
            // CartScreen(navigationActions)
        }
        
        composable(Destinations.Checkout.route) {
            // CheckoutScreen(navigationActions)
        }
        
        composable(Destinations.Payment.route) {
            // PaymentScreen(navigationActions)
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
            // OrderConfirmationScreen(orderId, navigationActions)
        }
        
        // User Profile Flow
        composable(Destinations.Profile.route) {
            // ProfileScreen(navigationActions)
        }
        
        composable(Destinations.OrderHistory.route) {
            // OrderHistoryScreen(navigationActions)
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
            // OrderDetailScreen(orderId, navigationActions)
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
