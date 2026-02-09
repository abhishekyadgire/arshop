package com.arshop.ui.screens.ar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arshop.ar.FootTrackingHelper
import com.arshop.ar.FootTrackingMode
import com.arshop.data.model.Product
import com.arshop.viewmodel.ARViewModel

/**
 * AR Screen for footwear try-on.
 * Supports floor placement and foot tracking modes.
 */
@Composable
fun ARFootwearScreen(
    product: Product,
    viewModel: ARViewModel
) {
    val context = LocalContext.current
    val footTrackingHelper = remember { FootTrackingHelper() }
    
    var trackingMode by remember { mutableStateOf(FootTrackingMode.FLOOR_PLACEMENT) }
    var shoeSize by remember { mutableFloatStateOf(9.0f) }
    var modelRotation by remember { mutableFloatStateOf(0f) }
    
    val floorDetected by footTrackingHelper.floorDetected.collectAsState()
    val footDetected by footTrackingHelper.footDetected.collectAsState()
    
    LaunchedEffect(trackingMode) {
        footTrackingHelper.setTrackingMode(trackingMode)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // AR Camera View with foot tracking
        ARCameraView(
            onARFrameUpdate = { frame, session ->
                footTrackingHelper.updateFootTracking(frame, session)
                
                // Place model at foot/floor position
                footTrackingHelper.getFootwearPlacementPose()?.let { pose ->
                    // TODO: Place 3D model at placement pose
                    // Apply scale based on shoe size
                    val scale = footTrackingHelper.getScaleFactor()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Floor detection status
        if (trackingMode == FootTrackingMode.FLOOR_PLACEMENT && !floorDetected) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Point camera at the floor",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Mode toggle button
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Mode",
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (trackingMode == FootTrackingMode.FLOOR_PLACEMENT)
                            Icons.Default.GridOn
                        else
                            Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (trackingMode == FootTrackingMode.FLOOR_PLACEMENT)
                            "Floor"
                        else
                            "Foot",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Switch(
                    checked = trackingMode == FootTrackingMode.FOOT_TRACKING,
                    onCheckedChange = { enabled ->
                        trackingMode = if (enabled)
                            FootTrackingMode.FOOT_TRACKING
                        else
                            FootTrackingMode.FLOOR_PLACEMENT
                    }
                )
            }
        }
        
        // Rotation controls
        Card(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rotate",
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rotate left
                IconButton(
                    onClick = {
                        modelRotation = (modelRotation - 15f) % 360f
                        // TODO: Apply rotation to model
                    }
                ) {
                    Icon(Icons.Default.RotateLeft, "Rotate left")
                }
                
                Text(
                    text = "${modelRotation.toInt()}°",
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Rotate right
                IconButton(
                    onClick = {
                        modelRotation = (modelRotation + 15f) % 360f
                        // TODO: Apply rotation to model
                    }
                ) {
                    Icon(Icons.Default.RotateRight, "Rotate right")
                }
            }
        }
        
        // Shoe size selector
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Shoe Size (US)",
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            shoeSize = (shoeSize - 0.5f).coerceAtLeast(5.0f)
                            footTrackingHelper.setShoeSize(shoeSize)
                        }
                    ) {
                        Icon(Icons.Default.Remove, "Decrease size")
                    }
                    
                    Text(
                        text = shoeSize.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    IconButton(
                        onClick = {
                            shoeSize = (shoeSize + 0.5f).coerceAtMost(15.0f)
                            footTrackingHelper.setShoeSize(shoeSize)
                        }
                    ) {
                        Icon(Icons.Default.Add, "Increase size")
                    }
                }
            }
        }
        
        // Tap to place hint
        if (floorDetected && !footTrackingHelper.isPlacementReady()) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap on the floor to place shoes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
