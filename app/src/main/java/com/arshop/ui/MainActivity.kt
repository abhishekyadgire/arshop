package com.arshop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arshop.ui.components.BottomNavigationBar
import com.arshop.ui.navigation.ARShopNavGraph
import com.arshop.ui.navigation.Destinations
import com.arshop.ui.navigation.bottomNavRoutes
import com.arshop.ui.theme.ARShopTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for ARShop application
 *
 * Sets up Compose UI with:
 * - Navigation graph
 * - Theme application
 * - Edge-to-edge display
 * - Bottom navigation bar (for applicable screens)
 * - Authentication state check
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ARShopTheme {
                ARShopApp()
            }
        }
    }
}

/**
 * Main app composable
 *
 * Handles:
 * - Navigation setup
 * - Start destination based on auth state
 * - Bottom navigation visibility
 * - Scaffold layout
 */
@Composable
fun ARShopApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine start destination based on auth state
    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) {
        Destinations.Home.route
    } else {
        Destinations.Login.route
    }
    
    // Check if current screen should show bottom nav
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(
                    currentRoute = currentRoute ?: Destinations.Home.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to home to avoid building a large back stack
                            popUpTo(Destinations.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            ARShopNavGraph(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}
