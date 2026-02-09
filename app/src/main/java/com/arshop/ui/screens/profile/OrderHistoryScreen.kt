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
 * Order history screen displaying list of user orders
 *
 * Features:
 * - List of orders with order cards
 * - Filter by status (All, Pending, Delivered, Cancelled)
 * - Each order shows: number, date, status, total
 * - Click to navigate to order detail
 * - Loading and empty states
 * - Pull to refresh
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel OrderViewModel for order data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navigationActions: NavigationActions,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Delivered", "Cancelled")
    
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }
    
    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "All" -> orders
            "Pending" -> viewModel.getPendingOrders()
            "Delivered" -> orders.filter { it.orderStatus == "Delivered" }
            "Cancelled" -> orders.filter { it.orderStatus == "Cancelled" }
            else -> orders
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }
            
            when {
                loading && orders.isEmpty() -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                error != null && orders.isEmpty() -> {
                    ErrorState(
                        message = error ?: "Failed to load orders",
                        onRetry = { viewModel.loadOrders() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                filteredOrders.isEmpty() -> {
                    EmptyState(
                        message = if (selectedFilter == "All") 
                            "No orders yet" 
                        else 
                            "No $selectedFilter orders",
                        onAction = if (selectedFilter != "All") {
                            { selectedFilter = "All" }
                        } else {
                            { navigationActions.navigateToBrowse() }
                        },
                        actionLabel = if (selectedFilter != "All") 
                            "Clear Filter" 
                        else 
                            "Start Shopping",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredOrders) { order ->
                            OrderCard(
                                order = order,
                                onClick = {
                                    navigationActions.navigateToOrderDetail(order.id)
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
 * Order card component
 */
@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Order Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id.take(8).uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OrderStatusBadge(status = order.orderStatus)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Date
            Text(
                text = formatDate(order.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order Items Summary
            Text(
                text = "${order.items.size} item${if (order.items.size > 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Order Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                PriceDisplay(
                    price = order.total,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // View Details Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Text("View Details")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Formats a timestamp to a readable date string
 */
private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "Unknown date"
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
private fun OrderHistoryScreenPreview() {
    ARShopTheme {
        Surface {
            Text("Order History", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
