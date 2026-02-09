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
import com.arshop.data.model.Product
import com.arshop.data.model.ProductColor
import com.arshop.ui.components.ColorSelector
import com.arshop.ui.components.SizeSelector
import com.arshop.viewmodel.AdminProductViewModel

/**
 * Edit Product Screen for Admin.
 * Updates existing product information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: String,
    onBack: () -> Unit,
    onProductUpdated: () -> Unit,
    onProductDeleted: () -> Unit,
    viewModel: AdminProductViewModel = hiltViewModel()
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Clothing") }
    var gender by remember { mutableStateOf("Unisex") }
    var subcategory by remember { mutableStateOf("") }
    var selectedSizes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedColors by remember { mutableStateOf<List<ProductColor>>(emptyList()) }
    var arEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val uploadedImages by viewModel.uploadedImages.collectAsState()
    val uploaded3DModel by viewModel.uploaded3DModel.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    // Load product
    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }
    
    LaunchedEffect(viewModel.selectedProduct.collectAsState().value) {
        viewModel.selectedProduct.value?.let { prod ->
            product = prod
            name = prod.name
            description = prod.description
            price = prod.price.toString()
            category = prod.category
            gender = prod.gender
            subcategory = prod.subcategory
            selectedSizes = prod.sizes
            selectedColors = prod.colors
            arEnabled = prod.isArEnabled
        }
    }
    
    // Navigate back on success
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onProductUpdated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(
                        onClick = {
                            val stock = mutableMapOf<String, Int>()
                            selectedSizes.forEach { size ->
                                selectedColors.forEach { color ->
                                    stock["$size-${color.name}"] = 
                                        product?.stock?.get("$size-${color.name}") ?: 10
                                }
                            }
                            
                            viewModel.updateProduct(
                                productId = productId,
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
                        },
                        enabled = !loading
                    ) {
                        Text("Update")
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
                    label = { Text("Product Name") },
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
                    label = { Text("Price") },
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
                    label = { Text("Subcategory") },
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
            
            // Current Images
            item {
                Text(
                    text = "Product Images",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            item {
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
            
            // AR Model Status
            if (arEnabled) {
                item {
                    Card {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ViewInAr,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AR enabled for this product")
                        }
                    }
                }
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Product") },
                text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteProduct(productId)
                            showDeleteDialog = false
                            onProductDeleted()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
