package com.arshop.ui.components.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arshop.ui.theme.ARShopTheme

/**
 * Image picker component for admin product images
 *
 * @param imageUri Current image URI
 * @param onImageSelected Callback when image is selected
 * @param onImageRemoved Callback when image is removed
 * @param enabled Whether the picker is enabled
 * @param modifier Modifier for the picker
 */
@Composable
fun ImagePicker(
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showOptions by remember { mutableStateOf(false) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // Handle camera result
        }
    }
    
    Box(modifier = modifier) {
        if (imageUri != null) {
            // Show selected image with remove button
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Box {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    IconButton(
                        onClick = onImageRemoved,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        } else {
            // Show picker button
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable(enabled = enabled) { showOptions = true },
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add image",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Add Image",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // Options dialog
    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Select Image Source") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose from Gallery")
                    }
                    
                    TextButton(
                        onClick = {
                            // Camera functionality would require file provider setup
                            showOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOptions = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagePickerPreview() {
    ARShopTheme {
        ImagePicker(
            imageUri = null,
            onImageSelected = {}
        )
    }
}
