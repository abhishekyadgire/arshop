package com.arshop.ui.screens.profile

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
import com.arshop.data.model.Order
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Order detail screen showing comprehensive order information
 *
 * Features:
 * - Order number and date
 * - Order status with timeline
 * - List of order items with images
 * - Shipping address display
 * - Payment method display
 * - Order summary (prices breakdown)
 * - Cancel order button (if applicable)
 * - Reorder button
 * - Loading and error states
 *
 * @param orderId ID of the order to display
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel OrderViewModel for order data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String?,
    navigationActions: NavigationActions,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val selectedOrder by viewModel.selectedOrder.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val cancelSuccess by viewModel.cancelSuccess.collectAsState()
    
    var showCancelDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(orderId) {
        orderId?.let { viewModel.loadOrderDetails(it) }
    }
    
    LaunchedEffect(cancelSuccess) {
        if (cancelSuccess) {
            navigationActions.navigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share order */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            loading && selectedOrder == null -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
            error != null && selectedOrder == null -> {
                ErrorState(
                    message = error ?: "Failed to load order details",
                    onRetry = { orderId?.let { viewModel.loadOrderDetails(it) } },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            selectedOrder != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    selectedOrder?.let { order ->
                        // Order Header
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Order #${order.id.take(8).uppercase()}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = formatDate(order.createdAt),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        
                                        OrderStatusBadge(status = order.orderStatus)
                                    }
                                }
                            }
                        }
                        
                        // Order Status Timeline
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Order Status",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    OrderStatusTimeline(
                                        currentStatus = order.orderStatus,
                                        statusHistory = order.statusHistory
                                    )
                                }
                            }
                        }
                        
                        // Order Items
                        item {
                            Text(
                                text = "Items (${order.items.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(order.items) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Product Image
                                    Surface(
                                        modifier = Modifier.size(80.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        // TODO: Load actual image
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = item.productName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Size: ${item.size} | Color: ${item.color}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Qty: ${item.quantity}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        PriceDisplay(
                                            price = item.price * item.quantity,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Shipping Address
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Shipping Address",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    val address = order.shippingAddress
                                    Text(text = address.fullName)
                                    Text(text = address.addressLine1)
                                    address.addressLine2?.let { if (it.isNotBlank()) Text(text = it) }
                                    Text(text = "${address.city}, ${address.state} ${address.zipCode}")
                                    Text(text = address.phoneNumber)
                                }
                            }
                        }
                        
                        // Payment Method
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Payment,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Payment Method",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Text(text = order.paymentMethod.replaceFirstChar { it.uppercase() })
                                    Text(
                                        text = "Status: ${order.paymentStatus}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    
                                    SummaryRow("Subtotal", order.subtotal)
                                    SummaryRow("Shipping", order.shipping)
                                    SummaryRow("Tax", order.tax)
                                    
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        PriceDisplay(
                                            price = order.total,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Action Buttons
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Reorder Button
                                Button(
                                    onClick = { viewModel.reorder(order.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reorder")
                                }
                                
                                // Cancel Button (only if order is pending)
                                if (order.orderStatus == "Pending" || order.orderStatus == "Confirmed") {
                                    OutlinedButton(
                                        onClick = { showCancelDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                EmptyState(
                    message = "Order not found",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
        
        // Cancel Order Dialog
        if (showCancelDialog) {
            ConfirmationDialog(
                title = "Cancel Order",
                message = "Are you sure you want to cancel this order?",
                onConfirm = {
                    orderId?.let { viewModel.cancelOrder(it) }
                    showCancelDialog = false
                },
                onDismiss = { showCancelDialog = false }
            )
        }
    }
}

/**
 * Order status timeline component
 */
@Composable
private fun OrderStatusTimeline(
    currentStatus: String,
    statusHistory: List<com.arshop.data.model.OrderStatusUpdate>
) {
    val statuses = listOf("Pending", "Confirmed", "Shipped", "Delivered")
    val currentIndex = statuses.indexOf(currentStatus).coerceAtLeast(0)
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEachIndexed { index, status ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = MaterialTheme.shapes.small,
                    color = if (index <= currentIndex)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (index < currentIndex) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (index <= currentIndex)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Summary row helper
 */
@Composable
private fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        PriceDisplay(price = amount, style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * Formats a timestamp to a readable date string
 */
private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "Unknown date"
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
private fun OrderDetailScreenPreview() {
    ARShopTheme {
        Surface {
            Text("Order Details", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
