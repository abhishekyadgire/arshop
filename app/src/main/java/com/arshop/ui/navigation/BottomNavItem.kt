package com.arshop.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a bottom navigation item
 *
 * @param route The navigation route
 * @param selectedIcon Icon to show when selected
 * @param unselectedIcon Icon to show when unselected
 * @param label Text label for the item
 * @param badge Optional badge count (e.g., cart items)
 */
data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val badge: Int? = null
)

/**
 * Bottom navigation items for the main app
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = Destinations.Home.route,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        label = "Home"
    ),
    BottomNavItem(
        route = Destinations.Browse.route,
        selectedIcon = Icons.Filled.ShoppingCart, // Using cart for browse, can be changed
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Browse"
    ),
    BottomNavItem(
        route = Destinations.Cart.route,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        label = "Cart"
    ),
    BottomNavItem(
        route = Destinations.Profile.route,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        label = "Profile"
    )
)
