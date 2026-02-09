package com.arshop.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arshop.ui.navigation.BottomNavItem
import com.arshop.ui.navigation.bottomNavItems
import com.arshop.ui.theme.ARShopTheme

/**
 * Bottom navigation bar component
 *
 * @param selectedRoute Currently selected route
 * @param onNavigate Callback when a navigation item is clicked
 * @param cartItemCount Number of items in cart (for badge)
 * @param modifier Modifier for the navigation bar
 */
@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    cartItemCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.label == "Cart" && cartItemCount > 0) {
                                Badge { Text(text = cartItemCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selectedRoute == item.route) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            },
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(text = item.label) },
                alwaysShowLabel = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    ARShopTheme {
        BottomNavigationBar(
            selectedRoute = "home",
            onNavigate = {},
            cartItemCount = 3
        )
    }
}
