package com.arshop.ui.components.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arshop.ui.theme.ARShopTheme

/**
 * Reusable confirmation dialog component
 *
 * @param title Dialog title
 * @param message Dialog message
 * @param confirmText Confirm button text
 * @param dismissText Dismiss button text
 * @param onConfirm Callback when confirm button is clicked
 * @param onDismiss Callback when dismiss button is clicked or dialog is dismissed
 * @param modifier Modifier for the dialog
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    ARShopTheme {
        ConfirmationDialog(
            title = "Delete Item",
            message = "Are you sure you want to delete this item from your cart?",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
