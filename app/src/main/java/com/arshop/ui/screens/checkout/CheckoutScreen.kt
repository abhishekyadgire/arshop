package com.arshop.ui.screens.checkout

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
import com.arshop.data.model.PaymentMethod
import com.arshop.ui.components.*
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.state.CheckoutStep
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.CheckoutViewModel

/**
 * Checkout screen with multi-step flow
 *
 * Features:
 * - Multi-step checkout process (Address → Payment → Review)
 * - Step indicators at top
 * - Cart summary section
 * - Address selection/form
 * - Payment method selection
 * - Order review section
 * - Place order button
 * - Loading states
 * - Navigate to order confirmation on success
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel CheckoutViewModel for checkout state management
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navigationActions: NavigationActions,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val checkoutState by viewModel.checkoutState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showAddressForm by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadCheckoutData()
    }
    
    // Handle successful order placement
    LaunchedEffect(checkoutState.orderId) {
        checkoutState.orderId?.let { orderId ->
            navigationActions.navigateToOrderConfirmation(orderId)
        }
    }
    
    // Convert CheckoutStep to int for step indicator
    val currentStepInt = checkoutState.currentStep.ordinal
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (checkoutState.canGoBack()) {
                            viewModel.previousStep()
                        } else {
                            navigationActions.navigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            when (checkoutState.currentStep) {
                                CheckoutStep.ADDRESS -> viewModel.nextStep()
                                CheckoutStep.PAYMENT -> viewModel.nextStep()
                                CheckoutStep.CONFIRMATION -> viewModel.placeOrder()
                                else -> {}
                            }
                        },
                        enabled = checkoutState.canProceed(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (checkoutState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = when (checkoutState.currentStep) {
                                    CheckoutStep.ADDRESS -> "Continue to Payment"
                                    CheckoutStep.PAYMENT -> "Review Order"
                                    CheckoutStep.CONFIRMATION -> "Place Order"
                                    else -> "Continue"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step Indicator
            item {
                CheckoutStepIndicator(
                    currentStep = currentStepInt,
                    steps = listOf("Address", "Payment", "Review")
                )
            }
            
            // Error Display
            if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error ?: "An error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            
            // Step Content
            when (checkoutState.currentStep) {
                CheckoutStep.ADDRESS -> {
                    // Address Step
                    item {
                        Text(
                            text = "Shipping Address",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (addresses.isEmpty()) {
                        item {
                            Card(
                                onClick = { showAddressForm = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Add New Address")
                                }
                            }
                        }
                    } else {
                        items(addresses) { address ->
                            AddressCard(
                                address = address,
                                isSelected = checkoutState.selectedAddress?.id == address.id,
                                onSelect = { viewModel.selectAddress(address) }
                            )
                        }
                        
                        item {
                            OutlinedButton(
                                onClick = { showAddressForm = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add New Address")
                            }
                        }
                    }
                }
                
                CheckoutStep.PAYMENT -> {
                    // Payment Step
                    item {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        // For now, provide simple payment method selection
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Stripe", "PayPal", "Razorpay", "Google Pay").forEach { method ->
                                val paymentMethod = PaymentMethod.createForType(method.lowercase())
                                Card(
                                    onClick = { viewModel.selectPayment(paymentMethod) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (checkoutState.selectedPayment?.type?.name?.lowercase() == method.lowercase())
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = checkoutState.selectedPayment?.type?.name?.lowercase() == method.lowercase(),
                                            onClick = { viewModel.selectPayment(paymentMethod) }
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(method)
                                    }
                                }
                            }
                        }
                    }
                }
                
                CheckoutStep.CONFIRMATION -> {
                    // Review Step
                    item {
                        Text(
                            text = "Review Order",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Order Items
                    items(cartItems) { item ->
                        OrderItemCard(item = item)
                    }
                    
                    // Shipping Address
                    item {
                        checkoutState.selectedAddress?.let { address ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Shipping Address",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(address.fullName)
                                    Text("${address.addressLine1}, ${address.addressLine2 ?: ""}")
                                    Text("${address.city}, ${address.state} ${address.zipCode}")
                                    Text(address.phoneNumber)
                                }
                            }
                        }
                    }
                    
                    // Order Summary
                    item {
                        OrderSummaryCard(
                            subtotal = checkoutState.cartTotal,
                            shipping = 0.0, // TODO: Calculate shipping
                            tax = 0.0, // TODO: Calculate tax
                            total = checkoutState.cartTotal
                        )
                    }
                }
                
                else -> {}
            }
            
            // Add bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Address Form Dialog
        if (showAddressForm) {
            // TODO: Implement address form dialog
            showAddressForm = false
        }
    }
}

/**
 * Checkout step indicator
 */
@Composable
private fun CheckoutStepIndicator(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = MaterialTheme.shapes.small,
                    color = if (index <= currentStep) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (index < currentStep) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                color = if (index <= currentStep) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (index <= currentStep) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (index < steps.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(bottom = 24.dp),
                    color = if (index < currentStep) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * Address card component
 */
@Composable
private fun AddressCard(
    address: com.arshop.data.model.Address,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = address.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("${address.addressLine1}, ${address.addressLine2 ?: ""}")
                Text("${address.city}, ${address.state} ${address.zipCode}")
                Text(address.phoneNumber)
            }
        }
    }
}

/**
 * Order item card for review step
 */
@Composable
private fun OrderItemCard(item: com.arshop.data.model.CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                // Product image placeholder
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Size: ${item.size} | Color: ${item.color}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Qty: ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            PriceDisplay(
                price = item.price * item.quantity,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

/**
 * Order summary card
 */
@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    shipping: Double,
    tax: Double,
    total: Double
) {
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
            
            SummaryRow("Subtotal", subtotal)
            SummaryRow("Shipping", shipping)
            SummaryRow("Tax", tax)
            
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
                    price = total,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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

@Preview(showBackground = true)
@Composable
private fun CheckoutScreenPreview() {
    ARShopTheme {
        Surface {
            Text("Checkout", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
