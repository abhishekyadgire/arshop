package com.arshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes
import com.arshop.ui.theme.DiscountBackground
import com.arshop.ui.theme.SaleRed

/**
 * Price display component with discount support
 *
 * @param price Current price
 * @param originalPrice Original price (null if no discount)
 * @param currency Currency symbol
 * @param showDiscountBadge Whether to show discount percentage badge
 * @param modifier Modifier for the component
 */
@Composable
fun PriceDisplay(
    price: Double,
    originalPrice: Double? = null,
    currency: String = "$",
    showDiscountBadge: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Current Price
        Text(
            text = "$currency${String.format("%.2f", price)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Original Price with strikethrough
        if (originalPrice != null && originalPrice > price) {
            Text(
                text = "$currency${String.format("%.2f", originalPrice)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
            )
            
            // Discount Badge
            if (showDiscountBadge) {
                val discountPercent = ((originalPrice - price) / originalPrice * 100).toInt()
                Box(
                    modifier = Modifier
                        .background(
                            color = SaleRed,
                            shape = CustomShapes.Badge
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "-$discountPercent%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PriceDisplayPreview() {
    ARShopTheme {
        PriceDisplay(
            price = 199.99,
            originalPrice = 299.99
        )
    }
}
