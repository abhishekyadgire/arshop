package com.arshop.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Size variants for loading indicators
 */
enum class LoadingSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Loading indicator component
 *
 * @param size Size of the indicator (Small, Medium, Large)
 * @param fullScreen Whether to fill the entire screen
 * @param modifier Modifier for the indicator
 */
@Composable
fun LoadingIndicator(
    size: LoadingSize = LoadingSize.MEDIUM,
    fullScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val indicatorSize: Dp = when (size) {
        LoadingSize.SMALL -> 24.dp
        LoadingSize.MEDIUM -> 48.dp
        LoadingSize.LARGE -> 64.dp
    }
    
    val containerModifier = if (fullScreen) {
        modifier.fillMaxSize()
    } else {
        modifier
    }
    
    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingIndicatorPreview() {
    ARShopTheme {
        LoadingIndicator(size = LoadingSize.MEDIUM)
    }
}
