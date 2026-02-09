package com.arshop.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arshop.data.model.ProductColor
import com.arshop.ui.components.ColorSelector
import com.arshop.ui.components.SizeSelector
import com.arshop.viewmodel.AdminProductViewModel

/**
 * Add Product Screen for Admin.
 * Form for creating new products with image and 3D model upload.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onProductCreated: () -> Unit,
    viewModel: AdminProductViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Clothing") }
    var gender by remember { mutableStateOf("Unisex") }
    var subcategory by remember { mutableStateOf("") }
    var selectedSizes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedColors by remember { mutableStateOf<List<ProductColor>>(emptyList()) }
    var arEnabled by remember { mutableStateOf(false) }
    
    val uploadedImages by viewModel.uploadedImages.collectAsState()
    val uploaded3DModel by viewModel.uploaded3DModel.collectAsState()
    val imageUploading by viewModel.imageUploading.collectAsState()
    val modelUploading by viewModel.modelUploading.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.uploadImages(uris)
        }
    }
    
    // 3D model picker
    val modelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.upload3DModel(it)
        }
    }
    
    // Navigate back on success
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onProductCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // Validate and create product
                            if (validateForm(name, price, selectedSizes, selectedColors)) {
                                // TODO: Create stock map from sizes and colors
                                val stock = mutableMapOf<String, Int>()
                                selectedSizes.forEach { size ->
                                    selectedColors.forEach { color ->
                                        stock["$size-${color.name}"] = 10 // Default stock
                                    }
                                }
                                
                                viewModel.createProduct(
                                    name = name,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    category = category,
                                    gender = gender,
                                    subcategory = subcategory,
                                    sizes = selectedSizes,
                                    colors = selectedColors,
                                    stock = stock
                                )
                            }
                        },
                        enabled = !loading
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Information
            item {
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
            
            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Text("$") },
                    singleLine = true
                )
            }
            
            // Category
            item {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Clothing", "Footwear").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = subcategory,
                    onValueChange = { subcategory = it },
                    label = { Text("Subcategory (e.g., Shirts, Sneakers)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // Gender
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Men", "Women", "Unisex").forEach { gen ->
                        FilterChip(
                            selected = gender == gen,
                            onClick = { gender = gen },
                            label = { Text(gen) }
                        )
                    }
                }
            }
            
            // Sizes
            item {
                Text(
                    text = "Sizes *",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                val availableSizes = if (category == "Clothing") {
                    listOf("XS", "S", "M", "L", "XL", "XXL")
                } else {
                    listOf("6", "7", "8", "9", "10", "11", "12")
                }
                
                SizeSelector(
                    sizes = availableSizes,
                    selectedSize = selectedSizes.firstOrNull() ?: "",
                    onSizeSelected = { size ->
                        selectedSizes = if (size in selectedSizes) {
                            selectedSizes - size
                        } else {
                            selectedSizes + size
                        }
                    }
                )
            }
            
            // Colors
            item {
                Text(
                    text = "Colors *",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                val availableColors = listOf(
                    ProductColor("Black", "#000000"),
                    ProductColor("White", "#FFFFFF"),
                    ProductColor("Red", "#FF0000"),
                    ProductColor("Blue", "#0000FF"),
                    ProductColor("Green", "#00FF00")
                )
                
                ColorSelector(
                    colors = availableColors,
                    selectedColor = selectedColors.firstOrNull() ?: availableColors[0],
                    onColorSelected = { color ->
                        selectedColors = if (color in selectedColors) {
                            selectedColors - color
                        } else {
                            selectedColors + color
                        }
                    }
                )
            }
            
            // Images
            item {
                Text(
                    text = "Product Images",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !imageUploading
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUploading) "Uploading..." else "Upload Images")
                    }
                    
                    if (uploadedImages.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uploadedImages) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 3D Model
            item {
                Text(
                    text = "AR 3D Model (Optional)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = arEnabled,
                            onCheckedChange = { arEnabled = it }
                        )
                        Text("Enable AR Try-On")
                    }
                    
                    if (arEnabled) {
                        Button(
                            onClick = { modelPickerLauncher.launch("*/*") },
                            enabled = !modelUploading
                        ) {
                            Icon(Icons.Default.ViewInAr, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (modelUploading) "Uploading..." else "Upload 3D Model (.glb)")
                        }
                        
                        uploaded3DModel?.let {
                            Text(
                                text = "Model uploaded successfully",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        
        // Error snackbar
        error?.let { errorMessage ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }
    }
}

private fun validateForm(
    name: String,
    price: String,
    sizes: List<String>,
    colors: List<ProductColor>
): Boolean {
    val priceValue = price.toDoubleOrNull()
    return name.isNotBlank() && 
           priceValue != null && 
           priceValue > 0 && 
           sizes.isNotEmpty() && 
           colors.isNotEmpty()
}
