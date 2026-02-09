package com.arshop.ui.screens.ar

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ar.ARScreenshotHelper
import com.arshop.data.model.Product
import com.arshop.viewmodel.ARViewModel
import com.arshop.viewmodel.TrackingState

/**
 * Main AR Try-On Screen.
 * Provides AR camera view for trying on clothing and footwear.
 * Supports both body tracking and floor placement modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARTryOnScreen(
    product: Product,
    onBack: () -> Unit,
    viewModel: ARViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val screenshotHelper = remember { ARScreenshotHelper(context) }
    
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(ARMode.AUTO) }
    
    val arEnabled by viewModel.arEnabled.collectAsState()
    val modelLoaded by viewModel.modelLoaded.collectAsState()
    val trackingState by viewModel.trackingState.collectAsState()
    val error by viewModel.error.collectAsState()
    val screenshot by viewModel.screenshot.collectAsState()
    
    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
        if (isGranted) {
            viewModel.initARSession()
            product.model3dUrl?.let { viewModel.loadModel(it) }
        } else {
            showPermissionRationale = true
        }
    }
    
    // Request permission on first launch
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    
    // Determine AR mode based on product category
    LaunchedEffect(product.category) {
        selectedMode = when (product.category) {
            "Clothing" -> ARMode.CLOTHING
            "Footwear" -> ARMode.FOOTWEAR
            else -> ARMode.AUTO
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AR Try-On") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                !cameraPermissionGranted -> {
                    PermissionRequiredContent(
                        showRationale = showPermissionRationale,
                        onRequestPermission = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
                
                error != null -> {
                    ErrorContent(
                        error = error ?: "",
                        onRetry = {
                            viewModel.clearError()
                            viewModel.initARSession()
                            product.model3dUrl?.let { viewModel.loadModel(it) }
                        }
                    )
                }
                
                !arEnabled || !modelLoaded -> {
                    LoadingContent()
                }
                
                else -> {
                    // Show AR camera view based on mode
                    when (selectedMode) {
                        ARMode.CLOTHING -> {
                            ARClothingScreen(
                                product = product,
                                viewModel = viewModel
                            )
                        }
                        ARMode.FOOTWEAR -> {
                            ARFootwearScreen(
                                product = product,
                                viewModel = viewModel
                            )
                        }
                        ARMode.AUTO -> {
                            // Auto-detect based on category
                            if (product.category == "Clothing") {
                                ARClothingScreen(product = product, viewModel = viewModel)
                            } else {
                                ARFootwearScreen(product = product, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
            
            // AR Controls Overlay
            if (arEnabled && modelLoaded) {
                ARControlsOverlay(
                    trackingState = trackingState,
                    onScreenshot = {
                        // TODO: Capture actual AR view bitmap
                        // For now, just trigger screenshot state
                    },
                    onShare = {
                        screenshot?.let { bitmap ->
                            // TODO: Share functionality
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            
            // Tracking State Indicator
            TrackingStateIndicator(
                trackingState = trackingState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            )
        }
    }
}

/**
 * Permission required content.
 */
@Composable
private fun PermissionRequiredContent(
    showRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (showRationale) {
                "AR try-on requires camera access to work. Please grant camera permission."
            } else {
                "To try on products in AR, we need access to your camera."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

/**
 * Error content.
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "AR Error",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

/**
 * Loading content.
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Initializing AR...")
        }
    }
}

/**
 * AR controls overlay.
 */
@Composable
private fun ARControlsOverlay(
    trackingState: TrackingState,
    onScreenshot: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Screenshot button
        FloatingActionButton(
            onClick = onScreenshot,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Camera, "Screenshot")
        }
        
        // Share button
        FloatingActionButton(
            onClick = onShare,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Default.Share, "Share")
        }
    }
}

/**
 * Tracking state indicator.
 */
@Composable
private fun TrackingStateIndicator(
    trackingState: TrackingState,
    modifier: Modifier = Modifier
) {
    if (trackingState != TrackingState.TRACKING) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (trackingState) {
                        TrackingState.INITIALIZING -> "Initializing AR..."
                        TrackingState.INSUFFICIENT_LIGHT -> "Need more light"
                        TrackingState.INSUFFICIENT_FEATURES -> "Move camera around"
                        TrackingState.PAUSED -> "AR Paused"
                        TrackingState.STOPPED -> "AR Stopped"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * AR modes for different product types.
 */
enum class ARMode {
    AUTO,
    CLOTHING,
    FOOTWEAR
}
