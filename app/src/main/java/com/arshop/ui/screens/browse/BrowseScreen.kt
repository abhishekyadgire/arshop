package com.arshop.ui.screens.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.BrowseViewModel

/**
 * Browse screen for exploring products with filters
 *
 * Features:
 * - Product grid with 2 columns
 * - Filter chips for category, gender, price range, AR-enabled
 * - Search bar at top
 * - Pagination support (load more on scroll)
 * - Loading and empty states
 * - Navigation to product detail
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel BrowseViewModel for product data and filtering
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    navigationActions: NavigationActions,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val arEnabledOnly by viewModel.arEnabledOnly.collectAsState()
    val hasActiveFilters by derivedStateOf { viewModel.hasActiveFilters() }
    
    var showFilterSheet by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navigationActions.navigateToSearch() }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { showFilterSheet = true }) {
                        Badge(
                            containerColor = if (hasActiveFilters) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter"
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
            // Filter Chips Row
            if (hasActiveFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedCategory != null) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setCategory(null) },
                            label = { Text(selectedCategory ?: "") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    if (selectedGender != null) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setGender(null) },
                            label = { Text(selectedGender ?: "") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    if (arEnabledOnly) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setArEnabledOnly(false) },
                            label = { Text("AR Only") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove filter",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text("Clear All")
                    }
                }
            }
            
            // Products Grid
            when {
                products.isEmpty() -> {
                    EmptyState(
                        message = if (hasActiveFilters) 
                            "No products match your filters" 
                        else 
                            "No products available",
                        onAction = if (hasActiveFilters) {
                            { viewModel.clearFilters() }
                        } else null,
                        actionLabel = if (hasActiveFilters) "Clear Filters" else null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products) { product ->
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
        
        // Filter Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false }
            ) {
                FilterBottomSheetContent(
                    viewModel = viewModel,
                    selectedCategory = selectedCategory,
                    selectedGender = selectedGender,
                    arEnabledOnly = arEnabledOnly,
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}

/**
 * Filter bottom sheet content
 */
@Composable
private fun FilterBottomSheetContent(
    viewModel: BrowseViewModel,
    selectedCategory: String?,
    selectedGender: String?,
    arEnabledOnly: Boolean,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filters",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Category Filter
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            listOf("Clothing", "Footwear", "Accessories").forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { 
                        viewModel.setCategory(if (selectedCategory == category) null else category)
                    },
                    label = { Text(category) }
                )
            }
        }
        
        // Gender Filter
        Text(
            text = "Gender",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            listOf("Men", "Women", "Unisex").forEach { gender ->
                FilterChip(
                    selected = selectedGender == gender,
                    onClick = { 
                        viewModel.setGender(if (selectedGender == gender) null else gender)
                    },
                    label = { Text(gender) }
                )
            }
        }
        
        // AR Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "AR-Enabled Only",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = arEnabledOnly,
                onCheckedChange = { viewModel.setArEnabledOnly(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Apply Button
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filters")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseScreenPreview() {
    ARShopTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Browse",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
