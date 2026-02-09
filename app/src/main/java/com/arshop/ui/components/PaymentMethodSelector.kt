package com.arshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Payment method option
 */
data class PaymentMethodOption(
    val id: String,
    val name: String,
    val icon: ImageVector
)

/**
 * Payment method selector component
 *
 * @param selectedMethod Currently selected payment method ID
 * @param onMethodSelected Callback when a payment method is selected
 * @param enabled Whether the selector is enabled
 * @param modifier Modifier for the selector
 */
@Composable
fun PaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val paymentMethods = listOf(
        PaymentMethodOption("stripe", "Credit/Debit Card (Stripe)", Icons.Default.CreditCard),
        PaymentMethodOption("paypal", "PayPal", Icons.Default.Payment),
        PaymentMethodOption("razorpay", "Razorpay", Icons.Default.Payment),
        PaymentMethodOption("googlepay", "Google Pay", Icons.Default.Payment)
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Payment Method",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        paymentMethods.forEach { method ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedMethod == method.id,
                        enabled = enabled,
                        onClick = { onMethodSelected(method.id) }
                    ),
                shape = MaterialTheme.shapes.medium,
                color = if (selectedMethod == method.id) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = if (selectedMethod == method.id) null else {
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = method.icon,
                        contentDescription = method.name,
                        tint = if (selectedMethod == method.id) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    Text(
                        text = method.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        color = if (selectedMethod == method.id) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    RadioButton(
                        selected = selectedMethod == method.id,
                        onClick = null,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentMethodSelectorPreview() {
    ARShopTheme {
        PaymentMethodSelector(
            selectedMethod = "stripe",
            onMethodSelected = {}
        )
    }
}
