package com.arshop.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.ProductDetailViewModel

/**
 * Product detail screen displaying comprehensive product information
 *
 * Features:
 * - Image carousel at top
 * - Product name, price, and rating
 * - Size and color selectors
 * - Quantity selector
 * - Add to cart button
 * - Try with AR button (for AR-enabled products)
 * - Product description and details
 * - Reviews section
 * - Loading and error states
 *
 * @param productId ID of the product to display
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel ProductDetailViewModel for product data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String?,
    navigationActions: NavigationActions,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val selectedSize by viewModel.selectedSize.collectAsState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val quantity by viewModel.quantity.collectAsState()
    val stockAvailable by viewModel.stockAvailable.collectAsState()
    val addToCartSuccess by viewModel.addToCartSuccess.collectAsState()
    
    LaunchedEffect(productId) {
        productId?.let { viewModel.loadProduct(it) }
    }
    
    // Navigate to cart after successful add
    LaunchedEffect(addToCartSuccess) {
        if (addToCartSuccess) {
            navigationActions.navigateToCart()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share product */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                    IconButton(onClick = { /* TODO: Add to favorites */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (product != null) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Try with AR Button (if AR-enabled)
                        if (product?.isArEnabled == true) {
                            OutlinedButton(
                                onClick = { 
                                    productId?.let { 
                                        navigationActions.navigateToARTryOn(it) 
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try AR")
                            }
                        }
                        
                        // Add to Cart Button
                        Button(
                            onClick = { viewModel.addToCart() },
                            enabled = viewModel.canAddToCart() && stockAvailable,
                            modifier = Modifier.weight(if (product?.isArEnabled == true) 1f else 2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            product == null -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Carousel
                    product?.let { prod ->
                        if (prod.images.isNotEmpty()) {
                            ImageCarousel(
                                images = prod.images,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                            )
                        }
                        
                        // Product Info
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Name and Price
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            PriceDisplay(
                                price = prod.price,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Rating
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RatingBar(
                                    rating = prod.rating,
                                    modifier = Modifier.height(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${prod.reviewCount} reviews)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Size Selector
                            if (prod.sizes.isNotEmpty()) {
                                Text(
                                    text = "Select Size",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                SizeSelector(
                                    sizes = prod.sizes,
                                    selectedSize = selectedSize,
                                    onSizeSelected = { viewModel.selectSize(it) }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Color Selector
                            if (prod.colors.isNotEmpty()) {
                                Text(
                                    text = "Select Color",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                ColorSelector(
                                    colors = prod.colors,
                                    selectedColor = selectedColor,
                                    onColorSelected = { viewModel.selectColor(it) }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            // Quantity Selector
                            Text(
                                text = "Quantity",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            QuantitySelector(
                                quantity = quantity,
                                onIncrement = { viewModel.incrementQuantity() },
                                onDecrement = { if (quantity > 1) viewModel.setQuantity(quantity - 1) },
                                maxQuantity = if (stockAvailable) 10 else 0
                            )
                            
                            if (!stockAvailable) {
                                Text(
                                    text = "Out of stock",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            HorizontalDivider()
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Description
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = prod.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Product Details
                            Text(
                                text = "Product Details",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            DetailRow("Category", prod.category)
                            DetailRow("Gender", prod.gender)
                            if (prod.isArEnabled) {
                                DetailRow("AR Enabled", "Yes")
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Product detail row component
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailScreenPreview() {
    ARShopTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Product Name",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
