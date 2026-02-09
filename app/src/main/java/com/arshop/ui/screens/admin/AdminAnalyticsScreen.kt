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
import com.arshop.ui.components.LoadingIndicator
import com.arshop.ui.components.PriceDisplay
import com.arshop.viewmodel.AdminViewModel

/**
 * Admin Analytics Screen.
 * Displays sales analytics, revenue charts, and performance metrics.
 * 
 * TODO: Add advanced charts using a charting library (e.g., Vico, MPAndroidChart)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val loading by viewModel.loading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadAnalytics()
        viewModel.loadOrders()
    }
    
    // Calculate analytics
    val totalRevenue = remember(orders) {
        orders.filter { it.orderStatus == "Delivered" }.sumOf { it.total }
    }
    
    val pendingRevenue = remember(orders) {
        orders.filter { it.orderStatus in listOf("Pending", "Processing", "Shipped") }
            .sumOf { it.total }
    }
    
    val totalOrders = orders.size
    val completedOrders = orders.count { it.orderStatus == "Delivered" }
    val pendingOrders = orders.count { it.orderStatus == "Pending" }
    
    // Order status distribution
    val statusDistribution = remember(orders) {
        orders.groupBy { it.orderStatus }
            .mapValues { it.value.size }
    }
    
    // Top selling products (placeholder - would need order items)
    val topProducts = remember(orders) {
        // TODO: Calculate from order items
        products.take(5)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Revenue Overview
                item {
                    Text(
                        text = "Revenue Overview",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            title = "Total Revenue",
                            value = "$${String.format("%.2f", totalRevenue)}",
                            icon = Icons.Default.AttachMoney,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        AnalyticsCard(
                            title = "Pending Revenue",
                            value = "$${String.format("%.2f", pendingRevenue)}",
                            icon = Icons.Default.Schedule,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Orders Overview
                item {
                    Text(
                        text = "Orders Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            title = "Total Orders",
                            value = totalOrders.toString(),
                            icon = Icons.Default.ShoppingCart,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        AnalyticsCard(
                            title = "Completed",
                            value = completedOrders.toString(),
                            icon = Icons.Default.CheckCircle,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        
                        AnalyticsCard(
                            title = "Pending",
                            value = pendingOrders.toString(),
                            icon = Icons.Default.PendingActions,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Order Status Distribution
                item {
                    Text(
                        text = "Order Status Distribution",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            statusDistribution.forEach { (status, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            modifier = Modifier.size(12.dp),
                                            color = when (status) {
                                                "Pending" -> MaterialTheme.colorScheme.error
                                                "Processing" -> MaterialTheme.colorScheme.primary
                                                "Shipped" -> MaterialTheme.colorScheme.tertiary
                                                "Delivered" -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = MaterialTheme.shapes.small
                                        ) {}
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Text(
                                            text = status,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    
                                    Text(
                                        text = "$count (${if (totalOrders > 0) String.format("%.1f", count * 100.0 / totalOrders) else "0.0"}%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Top Selling Products
                item {
                    Text(
                        text = "Top Products",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(topProducts) { product ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = product.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                PriceDisplay(price = product.price)
                                Text(
                                    text = "⭐ ${product.rating} (${product.reviewCount})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                
                // Analytics Note
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Analytics Enhancement",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Advanced charts and detailed analytics coming soon",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
