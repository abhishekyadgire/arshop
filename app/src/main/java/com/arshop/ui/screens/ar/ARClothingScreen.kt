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
import com.arshop.ar.BodyTrackingHelper
import com.arshop.ar.BodyType
import com.arshop.data.model.Product
import com.arshop.viewmodel.ARViewModel

/**
 * AR Screen for clothing try-on with body tracking.
 * Uses body detection to place clothing items on the user.
 */
@Composable
fun ARClothingScreen(
    product: Product,
    viewModel: ARViewModel
) {
    val context = LocalContext.current
    val bodyTrackingHelper = remember { BodyTrackingHelper() }
    
    var modelScale by remember { mutableFloatStateOf(1.0f) }
    var bodyType by remember { mutableStateOf(BodyType.UPPER_BODY) }
    
    val bodyDetected by bodyTrackingHelper.bodyDetected.collectAsState()
    val trackingQuality by bodyTrackingHelper.trackingQuality.collectAsState()
    
    // Determine body type based on subcategory
    LaunchedEffect(product.subcategory) {
        bodyType = when (product.subcategory.lowercase()) {
            "dress", "jumpsuit", "overall" -> BodyType.FULL_BODY
            else -> BodyType.UPPER_BODY
        }
        bodyTrackingHelper.setBodyType(bodyType)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // AR Camera View with body tracking
        ARCameraView(
            onARFrameUpdate = { frame, session ->
                bodyTrackingHelper.updateBodyTracking(frame, session)
                
                // Place model on body anchor if detected
                bodyTrackingHelper.getClothingAnchor()?.let { pose ->
                    // TODO: Place 3D model at body anchor
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Body tracking status
        if (!bodyDetected) {
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
                        imageVector = Icons.Default.PersonSearch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Position yourself in frame",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Size adjustment controls
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
                    text = "Size",
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Increase size
                IconButton(
                    onClick = {
                        modelScale = (modelScale + 0.1f).coerceAtMost(2.0f)
                        // TODO: Apply scale to model
                    }
                ) {
                    Icon(Icons.Default.Add, "Increase size")
                }
                
                Text(
                    text = "${(modelScale * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Decrease size
                IconButton(
                    onClick = {
                        modelScale = (modelScale - 0.1f).coerceAtLeast(0.5f)
                        // TODO: Apply scale to model
                    }
                ) {
                    Icon(Icons.Default.Remove, "Decrease size")
                }
            }
        }
        
        // Manual placement hint (when body not detected)
        if (!bodyDetected) {
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
                        text = "Tap to place manually",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Tracking quality indicator
        if (bodyDetected) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                color = when {
                    bodyTrackingHelper.isTrackingGood() -> 
                        MaterialTheme.colorScheme.primaryContainer
                    else -> 
                        MaterialTheme.colorScheme.errorContainer
                },
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (bodyTrackingHelper.isTrackingGood()) 
                            Icons.Default.CheckCircle 
                        else 
                            Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (bodyTrackingHelper.isTrackingGood())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tracking: ${trackingQuality.name}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
