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
import com.arshop.data.model.Order
import com.arshop.ui.components.LoadingIndicator
import com.arshop.ui.components.PriceDisplay
import com.arshop.viewmodel.AdminOrderViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Order Management Screen for Admin.
 * Lists and manages all orders with status updates.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    onBack: () -> Unit,
    onNavigateToOrderDetails: (String) -> Unit,
    viewModel: AdminOrderViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showStatusDialog by remember { mutableStateOf<Order?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }
    
    // Filter orders by status
    val filteredOrders = remember(orders, selectedStatusFilter) {
        if (selectedStatusFilter == "All") {
            orders
        } else {
            orders.filter { it.orderStatus == selectedStatusFilter }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Orders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Status filter
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statuses = listOf("All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled")
                items(statuses.size) { index ->
                    val status = statuses[index]
                    val count = if (status == "All") {
                        orders.size
                    } else {
                        orders.count { it.orderStatus == status }
                    }
                    
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { selectedStatusFilter = status },
                        label = { Text("$status ($count)") }
                    )
                }
            }
            
            Divider()
            
            // Orders list
            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
                
                filteredOrders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No orders found",
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
                        items(filteredOrders, key = { it.id }) { order ->
                            OrderCard(
                                order = order,
                                onUpdateStatus = { showStatusDialog = order },
                                onViewDetails = { onNavigateToOrderDetails(order.id) }
                            )
                        }
                    }
                }
            }
        }
        
        // Status update dialog
        showStatusDialog?.let { order ->
            OrderStatusDialog(
                order = order,
                onDismiss = { showStatusDialog = null },
                onUpdateStatus = { newStatus ->
                    viewModel.updateOrderStatus(order.id, newStatus)
                    showStatusDialog = null
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
private fun OrderCard(
    order: Order,
    onUpdateStatus: () -> Unit,
    onViewDetails: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
    
    Card(onClick = onViewDetails) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id.take(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(order.createdAt.toDate()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = when (order.orderStatus) {
                        "Pending" -> MaterialTheme.colorScheme.errorContainer
                        "Processing" -> MaterialTheme.colorScheme.primaryContainer
                        "Shipped" -> MaterialTheme.colorScheme.tertiaryContainer
                        "Delivered" -> MaterialTheme.colorScheme.secondaryContainer
                        "Cancelled" -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = order.orderStatus,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Customer info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.userId.take(8),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.items.size} item(s)",
                    style = MaterialTheme.typography.bodyMedium
                )
                PriceDisplay(price = order.total, style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onUpdateStatus) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Status")
                }
            }
        }
    }
}

@Composable
private fun OrderStatusDialog(
    order: Order,
    onDismiss: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(order.orderStatus) }
    val statuses = listOf("Pending", "Processing", "Shipped", "Delivered", "Cancelled")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Order Status") },
        text = {
            Column {
                Text("Order #${order.id.take(8)}")
                Spacer(modifier = Modifier.height(16.dp))
                
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onUpdateStatus(selectedStatus) }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
