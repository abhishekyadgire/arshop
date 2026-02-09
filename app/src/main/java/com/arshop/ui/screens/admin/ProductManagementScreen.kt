package com.arshop.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arshop.data.model.Product
import com.arshop.ui.components.LoadingIndicator
import com.arshop.ui.components.PriceDisplay
import com.arshop.viewmodel.AdminProductViewModel

/**
 * Product Management Screen for Admin.
 * Lists all products with edit and delete functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AdminProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }
    
    // Filter products
    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesSearch = product.name.contains(searchQuery, ignoreCase = true) ||
                              product.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Products") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddProduct) {
                Icon(Icons.Default.Add, "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and filter
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search products...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Category filter
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Clothing", "Footwear").forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }
            }
            
            Divider()
            
            // Products list
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
                
                filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isEmpty()) "No products yet" else "No products found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            AdminProductCard(
                                product = product,
                                onEdit = { onNavigateToEditProduct(product.id) },
                                onDelete = { showDeleteDialog = product }
                            )
                        }
                    }
                }
            }
        }
        
        // Delete confirmation dialog
        showDeleteDialog?.let { product ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Product") },
                text = { Text("Are you sure you want to delete '${product.name}'? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteProduct(product.id)
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Error snackbar
        error?.let { errorMessage ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
private fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Product image
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                PriceDisplay(price = product.price)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = if (product.hasStock()) 
                            MaterialTheme.colorScheme.primaryContainer
                        else 
                            MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = if (product.hasStock()) "In Stock" else "Out of Stock",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    if (product.isArEnabled) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "AR",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
            
            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
