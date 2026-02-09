package com.arshop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Filter chip component for filtering product lists
 *
 * @param label Filter label text
 * @param selected Whether the filter is selected
 * @param onClick Callback when filter is clicked
 * @param showClearButton Whether to show clear/close button when selected
 * @param modifier Modifier for the chip
 */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    showClearButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        leadingIcon = if (selected && showClearButton) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear filter",
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        } else null,
        modifier = modifier,
        shape = CustomShapes.FilterChip,
        border = if (!selected) {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MaterialTheme.colorScheme.outline
            )
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun FilterChipPreview() {
    ARShopTheme {
        FilterChip(
            label = "Electronics",
            selected = true,
            onClick = {}
        )
    }
}
