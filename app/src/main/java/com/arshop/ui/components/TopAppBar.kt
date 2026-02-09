package com.arshop.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arshop.ui.theme.ARShopTheme

/**
 * Reusable top app bar component
 *
 * @param title Title text
 * @param showBackButton Whether to show back button
 * @param onBackClick Callback for back button click
 * @param actions Action buttons
 * @param modifier Modifier for the app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        } else {
            {}
        },
        actions = { actions() },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TopAppBarPreview() {
    ARShopTheme {
        TopAppBar(
            title = "Product Details",
            showBackButton = true,
            onBackClick = {}
        )
    }
}
