package com.arshop.ui.screens.cart

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.CartViewModel

/**
 * Cart screen displaying shopping cart items and order summary
 *
 * Features:
 * - List of cart items with images and details
 * - Quantity controls for each item
 * - Remove item functionality
 * - Order summary (subtotal, tax, shipping, total)
 * - Proceed to checkout button
 * - Empty cart state
 * - Loading states
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel CartViewModel for cart data management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navigationActions: NavigationActions,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val shipping by viewModel.shipping.collectAsState()
    val tax by viewModel.tax.collectAsState()
    val total by viewModel.total.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val itemCount by viewModel.itemCount.collectAsState()
    
    var itemToRemove by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Shopping Cart ${if (itemCount > 0) "($itemCount)" else ""}") 
                },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { navigationActions.navigateToCheckout() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !loading
                        ) {
                            Text(
                                text = "Proceed to Checkout",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            loading && cartItems.isEmpty() -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
            cartItems.isEmpty() -> {
                EmptyState(
                    message = "Your cart is empty",
                    onAction = { navigationActions.navigateToBrowse() },
                    actionLabel = "Start Shopping",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cart Items
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Product Image
                                // TODO: Use actual image loading
                                Surface(
                                    modifier = Modifier.size(80.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    // Placeholder for product image
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // Product Details
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = item.productName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = "Size: ${item.size} | Color: ${item.color}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    PriceDisplay(
                                        price = item.price,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Quantity Controls
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuantitySelector(
                                            quantity = item.quantity,
                                            onIncrement = { 
                                                viewModel.incrementQuantity(item.id)
                                            },
                                            onDecrement = { 
                                                viewModel.decrementQuantity(item.id)
                                            }
                                        )
                                        
                                        Spacer(modifier = Modifier.weight(1f))
                                        
                                        IconButton(
                                            onClick = { itemToRemove = item.id }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove item",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Order Summary
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Order Summary",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                SummaryRow("Subtotal", subtotal)
                                SummaryRow("Shipping", shipping)
                                SummaryRow("Tax", tax)
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    PriceDisplay(
                                        price = total,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    // Add bottom spacing for the button
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        // Remove Item Confirmation Dialog
        if (itemToRemove != null) {
            ConfirmationDialog(
                title = "Remove Item",
                message = "Are you sure you want to remove this item from your cart?",
                onConfirm = {
                    itemToRemove?.let { viewModel.removeItem(it) }
                    itemToRemove = null
                },
                onDismiss = { itemToRemove = null }
            )
        }
    }
}

/**
 * Order summary row component
 */
@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        PriceDisplay(
            price = amount,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {
    ARShopTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Shopping Cart",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
