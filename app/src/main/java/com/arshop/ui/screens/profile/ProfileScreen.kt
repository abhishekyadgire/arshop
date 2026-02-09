package com.arshop.ui.screens.profile

import androidx.compose.foundation.clickable
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
import com.arshop.viewmodel.ProfileViewModel

/**
 * Profile screen displaying user information and settings
 *
 * Features:
 * - User info display (name, email, photo)
 * - Edit profile button
 * - Addresses section with list
 * - Orders section (navigate to order history)
 * - Settings options
 * - Logout button
 * - Loading and error states
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel ProfileViewModel for user data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { /* TODO: Edit profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit profile"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            loading && user == null -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
            error != null && user == null -> {
                ErrorState(
                    message = error ?: "Failed to load profile",
                    onRetry = { viewModel.loadProfile() },
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
                    // User Info Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User Avatar
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "User avatar",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // User Details
                                Column {
                                    Text(
                                        text = user?.displayName ?: "User",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = user?.email ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    // Orders Section
                    item {
                        Text(
                            text = "Orders",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Card(
                            onClick = { navigationActions.navigateToOrderHistory() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ProfileMenuItem(
                                icon = Icons.Default.ShoppingBag,
                                title = "Order History",
                                subtitle = "View all your orders",
                                onClick = { navigationActions.navigateToOrderHistory() }
                            )
                        }
                    }
                    
                    // Addresses Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saved Addresses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = { /* TODO: Add address */ }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add")
                            }
                        }
                    }
                    
                    if (addresses.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No saved addresses",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(addresses.take(2)) { address ->
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
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = address.fullName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (address.isDefault) {
                                            Badge {
                                                Text("Default")
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${address.addressLine1}, ${address.addressLine2 ?: ""}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${address.city}, ${address.state} ${address.zipCode}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = address.phoneNumber,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        if (addresses.size > 2) {
                            item {
                                TextButton(
                                    onClick = { /* TODO: View all addresses */ },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("View All Addresses (${addresses.size})")
                                }
                            }
                        }
                    }
                    
                    // Settings Section
                    item {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                ProfileMenuItem(
                                    icon = Icons.Default.Notifications,
                                    title = "Notifications",
                                    subtitle = "Manage notification preferences",
                                    onClick = { /* TODO: Notifications */ }
                                )
                                HorizontalDivider()
                                ProfileMenuItem(
                                    icon = Icons.Default.Lock,
                                    title = "Privacy & Security",
                                    subtitle = "Manage your privacy settings",
                                    onClick = { /* TODO: Privacy */ }
                                )
                                HorizontalDivider()
                                ProfileMenuItem(
                                    icon = Icons.Default.Help,
                                    title = "Help & Support",
                                    subtitle = "Get help and contact support",
                                    onClick = { /* TODO: Help */ }
                                )
                            }
                        }
                    }
                    
                    // Logout Button
                    item {
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        
        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            ConfirmationDialog(
                title = "Logout",
                message = "Are you sure you want to logout?",
                onConfirm = {
                    viewModel.signOut()
                    navigationActions.clearBackStackAndNavigateToLogin()
                    showLogoutDialog = false
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }
}

/**
 * Profile menu item component
 */
@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ARShopTheme {
        Surface {
            Text("Profile", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
