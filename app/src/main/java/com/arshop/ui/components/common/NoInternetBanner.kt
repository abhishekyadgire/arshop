package com.arshop.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Banner shown when device is offline
 *
 * @param isOffline Whether device is offline
 * @param onRetry Callback for retry button click
 * @param dismissible Whether the banner can be dismissed
 * @param modifier Modifier for the banner
 */
@Composable
fun NoInternetBanner(
    isOffline: Boolean,
    onRetry: () -> Unit = {},
    dismissible: Boolean = false,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No internet",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = "No internet connection",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                
                TextButton(onClick = onRetry) {
                    Text(
                        text = "Retry",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoInternetBannerPreview() {
    ARShopTheme {
        NoInternetBanner(
            isOffline = true,
            onRetry = {}
        )
    }
}
