package com.arshop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Color option data class
 */
data class ColorOption(
    val name: String,
    val color: Color
)

/**
 * Color selector component for product colors
 *
 * @param colors Available colors
 * @param selectedColor Currently selected color name
 * @param onColorSelected Callback when color is selected
 * @param enabled Whether the selector is enabled
 * @param modifier Modifier for the selector
 */
@Composable
fun ColorSelector(
    colors: List<ColorOption>,
    selectedColor: String?,
    onColorSelected: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.titleMedium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colors.forEach { colorOption ->
                val isSelected = colorOption.name == selectedColor
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            ),
                            shape = CircleShape
                        )
                        .clickable(enabled = enabled) {
                            onColorSelected(colorOption.name)
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(colorOption.color)
                    )
                }
            }
        }
        
        if (selectedColor != null) {
            Text(
                text = "Selected: $selectedColor",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorSelectorPreview() {
    ARShopTheme {
        ColorSelector(
            colors = listOf(
                ColorOption("Black", Color.Black),
                ColorOption("White", Color.White),
                ColorOption("Blue", Color.Blue),
                ColorOption("Red", Color.Red)
            ),
            selectedColor = "Black",
            onColorSelected = {}
        )
    }
}
