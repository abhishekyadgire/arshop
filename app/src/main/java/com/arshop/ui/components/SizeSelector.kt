package com.arshop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Size selector component for product sizes
 *
 * @param sizes Available sizes
 * @param selectedSize Currently selected size
 * @param onSizeSelected Callback when size is selected
 * @param availableSizes Set of available (in stock) sizes
 * @param enabled Whether the selector is enabled
 * @param modifier Modifier for the selector
 */
@Composable
fun SizeSelector(
    sizes: List<String>,
    selectedSize: String?,
    onSizeSelected: (String) -> Unit,
    availableSizes: Set<String> = sizes.toSet(),
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Size",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sizes.forEach { size ->
                val isAvailable = size in availableSizes
                val isSelected = size == selectedSize
                
                Surface(
                    modifier = Modifier
                        .selectable(
                            selected = isSelected,
                            enabled = enabled && isAvailable,
                            onClick = { if (isAvailable) onSizeSelected(size) }
                        )
                        .size(56.dp),
                    shape = CustomShapes.SizeChip,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        !isAvailable -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surface
                    },
                    border = if (!isSelected) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    } else null
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = size,
                            style = MaterialTheme.typography.titleSmall,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                !isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
        
        if (selectedSize != null && selectedSize !in availableSizes) {
            Text(
                text = "Selected size is out of stock",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SizeSelectorPreview() {
    ARShopTheme {
        SizeSelector(
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            selectedSize = "M",
            onSizeSelected = {},
            availableSizes = setOf("S", "M", "L", "XL")
        )
    }
}
