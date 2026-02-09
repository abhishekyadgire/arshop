package com.arshop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Quantity selector component with increment/decrement buttons
 *
 * @param quantity Current quantity
 * @param onQuantityChange Callback when quantity changes
 * @param minQuantity Minimum allowed quantity
 * @param maxQuantity Maximum allowed quantity
 * @param enabled Whether the selector is enabled
 * @param modifier Modifier for the selector
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    minQuantity: Int = 1,
    maxQuantity: Int = 99,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CustomShapes.QuantitySelector,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrement Button
            IconButton(
                onClick = { onQuantityChange((quantity - 1).coerceAtLeast(minQuantity)) },
                enabled = enabled && quantity > minQuantity,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity"
                )
            }
            
            // Quantity Display
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.widthIn(min = 24.dp)
            )
            
            // Increment Button
            IconButton(
                onClick = { onQuantityChange((quantity + 1).coerceAtMost(maxQuantity)) },
                enabled = enabled && quantity < maxQuantity,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantitySelectorPreview() {
    ARShopTheme {
        QuantitySelector(
            quantity = 2,
            onQuantityChange = {}
        )
    }
}
