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
import com.arshop.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Admin Dashboard Screen.
 * Shows overview of products, orders, and analytics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val isAdmin by viewModel.isAdmin.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Check admin status (should be done earlier, but here as backup)
    LaunchedEffect(Unit) {
        // TODO: Get current user ID
        // viewModel.checkAdminStatus(userId)
        viewModel.loadOrders()
        viewModel.loadAnalytics()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct
            ) {
                Icon(Icons.Default.Add, "Add Product")
            }
        }
    ) { padding ->
        if (!isAdmin) {
            // Not authorized
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Admin Access Required",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        } else if (loading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Statistics Cards
                item {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Products",
                            value = products.size.toString(),
                            icon = Icons.Default.Inventory,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToProducts
                        )
                        
                        StatCard(
                            title = "Orders",
                            value = orders.size.toString(),
                            icon = Icons.Default.ShoppingCart,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToOrders
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Revenue",
                            value = "$${String.format("%.2f", viewModel.getTotalRevenue())}",
                            icon = Icons.Default.AttachMoney,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAnalytics
                        )
                        
                        StatCard(
                            title = "Pending",
                            value = viewModel.getPendingOrdersCount().toString(),
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToOrders
                        )
                    }
                }
                
                // Quick Actions
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "Manage Products",
                        description = "Add, edit, or remove products",
                        icon = Icons.Default.Inventory,
                        onClick = onNavigateToProducts
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "View Orders",
                        description = "Manage and update order status",
                        icon = Icons.Default.ShoppingBag,
                        onClick = onNavigateToOrders
                    )
                }
                
                item {
                    QuickActionCard(
                        title = "Analytics",
                        description = "View sales and performance metrics",
                        icon = Icons.Default.Analytics,
                        onClick = onNavigateToAnalytics
                    )
                }
                
                // Recent Orders
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent Orders",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(viewModel.getRecentOrders()) { order ->
                    RecentOrderCard(order = order, onClick = onNavigateToOrders)
                }
            }
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
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun RecentOrderCard(
    order: Order,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    
    Card(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Order #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(order.createdAt.toDate()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                PriceDisplay(price = order.total)
                Surface(
                    color = when (order.orderStatus) {
                        "Pending" -> MaterialTheme.colorScheme.errorContainer
                        "Processing" -> MaterialTheme.colorScheme.primaryContainer
                        "Shipped" -> MaterialTheme.colorScheme.tertiaryContainer
                        "Delivered" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = order.orderStatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
