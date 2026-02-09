package com.arshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Empty state component for displaying empty content states
 *
 * @param icon Icon to display
 * @param title Title text
 * @param description Description text
 * @param actionLabel Optional action button label
 * @param onActionClick Optional action button click callback
 * @param modifier Modifier for the component
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionLabel != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onActionClick) {
                Text(text = actionLabel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    ARShopTheme {
        EmptyState(
            icon = Icons.Default.ShoppingCart,
            title = "Your cart is empty",
            description = "Start adding items to your cart to see them here",
            actionLabel = "Start Shopping",
            onActionClick = {}
        )
    }
}
