package com.arshop.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.data.model.Category
import com.arshop.data.model.Product
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * Home screen displaying featured products and categories
 *
 * Features:
 * - Featured products section with horizontal scrolling
 * - Categories section with chips
 * - Pull-to-refresh functionality
 * - Loading and error states
 * - Navigation to product detail and browse screens
 * - Search button in top bar
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel HomeViewModel for data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationActions: NavigationActions,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val featuredProducts by viewModel.featuredProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val refreshing by viewModel.refreshing.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadFeaturedProducts()
        viewModel.loadCategories()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "ARShop",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { navigationActions.navigateToSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(refreshing),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(paddingValues)
        ) {
            when {
                loading && featuredProducts.isEmpty() -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                error != null && featuredProducts.isEmpty() -> {
                    ErrorState(
                        message = error ?: "An error occurred",
                        onRetry = { viewModel.loadFeaturedProducts() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Categories Section
                        if (categories.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Shop by Category",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(categories) { category ->
                                        CategoryChip(
                                            category = category,
                                            onClick = {
                                                // Navigate to browse with category filter
                                                navigationActions.navigateToBrowse()
                                            }
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        
                        // Featured Products Section
                        if (featuredProducts.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Featured Products",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    TextButton(onClick = { navigationActions.navigateToBrowse() }) {
                                        Text("See All")
                                    }
                                }
                            }
                            
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(featuredProducts) { product ->
                                        ProductCard(
                                            product = product,
                                            onClick = { 
                                                navigationActions.navigateToProductDetail(product.id)
                                            },
                                            modifier = Modifier.width(180.dp)
                                        )
                                    }
                                }
                            }
                            
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        
                        // AR-Enabled Products Section
                        item {
                            val arProducts = featuredProducts.filter { it.isArEnabled }
                            if (arProducts.isNotEmpty()) {
                                Text(
                                    text = "Try with AR",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    items(arProducts) { product ->
                                        ProductCard(
                                            product = product,
                                            onClick = { 
                                                navigationActions.navigateToProductDetail(product.id)
                                            },
                                            modifier = Modifier.width(180.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Empty State
                        if (featuredProducts.isEmpty() && categories.isEmpty() && !loading) {
                            item {
                                EmptyState(
                                    message = "No products available",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    ARShopTheme {
        Surface {
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = "Shop by Category",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = "Featured Products",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
