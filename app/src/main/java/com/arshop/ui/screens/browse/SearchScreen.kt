package com.arshop.ui.screens.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.BrowseViewModel

/**
 * Search screen for finding products
 *
 * Features:
 * - Search bar with auto-focus
 * - Recent searches display
 * - Search results as product list
 * - Clear search button
 * - Loading state
 * - Empty state when no results
 * - Navigation to product detail
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel BrowseViewModel for search functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigationActions: NavigationActions,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.filteredProducts.collectAsState()
    val focusRequester = remember { FocusRequester() }
    
    // Auto-focus search field on screen load
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { 
                            searchQuery = it
                            viewModel.search(it)
                        },
                        onSearch = { viewModel.search(searchQuery) },
                        placeholder = "Search products...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = ""
                            viewModel.search("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                searchQuery.isEmpty() -> {
                    // Recent Searches / Suggestions
                    RecentSearchesContent()
                }
                searchResults.isEmpty() -> {
                    EmptyState(
                        message = "No products found for \"$searchQuery\"",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    // Search Results
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "${searchResults.size} results for \"$searchQuery\"",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        items(searchResults) { product ->
                            ProductCard(
                                product = product,
                                onClick = {
                                    navigationActions.navigateToProductDetail(product.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recent searches content shown when search is empty
 */
@Composable
private fun RecentSearchesContent() {
    // TODO: Implement recent searches from local storage
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Recent Searches",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "No recent searches",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Popular Searches",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val popularSearches = listOf(
            "Sneakers",
            "T-Shirts",
            "Jeans",
            "Dresses",
            "Hoodies"
        )
        
        popularSearches.forEach { search ->
            SuggestionChip(
                onClick = { /* Handle search */ },
                label = { Text(search) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    ARShopTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
