package com.arshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.*

/**
 * Order status badge component
 *
 * @param status Order status string
 * @param modifier Modifier for the badge
 */
@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (status.uppercase()) {
        "PENDING" -> StatusPending to Icons.Default.Schedule
        "PROCESSING" -> StatusProcessing to Icons.Default.Autorenew
        "SHIPPED" -> StatusShipped to Icons.Default.LocalShipping
        "DELIVERED" -> StatusDelivered to Icons.Default.CheckCircle
        "CANCELLED" -> StatusCancelled to Icons.Default.Cancel
        else -> MaterialTheme.colorScheme.outline to Icons.Default.Info
    }
    
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = CustomShapes.Badge
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderStatusBadgePreview() {
    ARShopTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OrderStatusBadge(status = "Pending")
            OrderStatusBadge(status = "Processing")
            OrderStatusBadge(status = "Shipped")
            OrderStatusBadge(status = "Delivered")
            OrderStatusBadge(status = "Cancelled")
        }
    }
}
