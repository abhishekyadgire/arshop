package com.arshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
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
 * Error state component for displaying error messages
 *
 * @param message Error message
 * @param icon Error icon (default: Error icon)
 * @param showRetryButton Whether to show retry button
 * @param onRetryClick Callback for retry button click
 * @param modifier Modifier for the component
 */
@Composable
fun ErrorState(
    message: String,
    icon: ImageVector = Icons.Default.Error,
    showRetryButton: Boolean = true,
    onRetryClick: (() -> Unit)? = null,
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
            contentDescription = "Error",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (showRetryButton && onRetryClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    ARShopTheme {
        ErrorState(
            message = "Failed to load products. Please check your connection and try again.",
            onRetryClick = {}
        )
    }
}
