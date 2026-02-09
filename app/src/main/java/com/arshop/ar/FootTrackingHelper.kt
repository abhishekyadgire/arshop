package com.arshop.ar

import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Helper class for foot tracking in AR for footwear try-on.
 * Supports two modes:
 * 1. Floor plane placement - Places shoes on detected floor
 * 2. Foot tracking - Tracks foot position for realistic placement
 * 
 * TODO: Implement ML Kit-based foot detection for precise tracking
 */
class FootTrackingHelper {
    
    private val _floorDetected = MutableStateFlow(false)
    val floorDetected: StateFlow<Boolean> = _floorDetected.asStateFlow()
    
    private val _floorPlane = MutableStateFlow<Plane?>(null)
    val floorPlane: StateFlow<Plane?> = _floorPlane.asStateFlow()
    
    private val _footDetected = MutableStateFlow(false)
    val footDetected: StateFlow<Boolean> = _footDetected.asStateFlow()
    
    private val _footPose = MutableStateFlow<Pose?>(null)
    val footPose: StateFlow<Pose?> = _footPose.asStateFlow()
    
    private val _trackingMode = MutableStateFlow(FootTrackingMode.FLOOR_PLACEMENT)
    val trackingMode: StateFlow<FootTrackingMode> = _trackingMode.asStateFlow()
    
    private val _placementPose = MutableStateFlow<Pose?>(null)
    val placementPose: StateFlow<Pose?> = _placementPose.asStateFlow()
    
    private val _shoeSize = MutableStateFlow(9.0f)
    val shoeSize: StateFlow<Float> = _shoeSize.asStateFlow()
    
    /**
     * Updates foot tracking from current AR frame.
     * @param frame Current AR frame
     * @param session AR session
     */
    fun updateFootTracking(frame: Frame, session: Session) {
        try {
            val camera = frame.camera
            
            if (camera.trackingState == TrackingState.TRACKING) {
                when (_trackingMode.value) {
                    FootTrackingMode.FLOOR_PLACEMENT -> {
                        updateFloorPlacement(frame)
                    }
                    FootTrackingMode.FOOT_TRACKING -> {
                        updateFootDetection(frame)
                    }
                }
            } else {
                _floorDetected.value = false
                _footDetected.value = false
            }
            
        } catch (e: Exception) {
            _floorDetected.value = false
            _footDetected.value = false
        }
    }
    
    /**
     * Updates floor plane detection and placement.
     */
    private fun updateFloorPlacement(frame: Frame) {
        // Find horizontal planes (floor)
        val planes = frame.getUpdatedTrackables(Plane::class.java)
            .filter { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
            .filter { it.trackingState == TrackingState.TRACKING }
        
        if (planes.isNotEmpty()) {
            // Use largest detected plane as floor
            val floorPlane = planes.maxByOrNull { it.extentX * it.extentZ }
            
            _floorPlane.value = floorPlane
            _floorDetected.value = true
            
            // Set placement pose at center of detected plane
            floorPlane?.let {
                _placementPose.value = it.centerPose
            }
        } else {
            _floorDetected.value = false
        }
    }
    
    /**
     * Updates foot detection using ML-based tracking.
     * TODO: Integrate ML Kit Pose Detection for actual foot tracking
     */
    private fun updateFootDetection(frame: Frame) {
        // TODO: Implement ML Kit Pose Detection for feet
        // For now, use floor plane + estimated foot position
        
        if (_floorDetected.value && _floorPlane.value != null) {
            // Estimate foot position on floor plane
            val camera = frame.camera
            val cameraPose = camera.pose
            
            // Estimate foot position below camera view
            val footOffset = Pose.makeTranslation(0f, -1.5f, -0.5f)
            val estimatedFootPose = cameraPose.compose(footOffset)
            
            // Project onto floor plane
            _floorPlane.value?.let { plane ->
                val footPose = projectPoseOntoPlane(estimatedFootPose, plane)
                _footPose.value = footPose
                _placementPose.value = footPose
                _footDetected.value = true
            }
        } else {
            _footDetected.value = false
        }
    }
    
    /**
     * Projects a pose onto a plane.
     */
    private fun projectPoseOntoPlane(pose: Pose, plane: Plane): Pose {
        val centerPose = plane.centerPose
        val planeY = centerPose.ty()
        
        // Project pose onto plane's Y coordinate
        return Pose.makeTranslation(
            pose.tx(),
            planeY,
            pose.tz()
        ).compose(pose.extractRotation())
    }
    
    /**
     * Sets tracking mode (floor placement vs foot tracking).
     */
    fun setTrackingMode(mode: FootTrackingMode) {
        _trackingMode.value = mode
        reset()
    }
    
    /**
     * Gets the placement pose for footwear.
     */
    fun getFootwearPlacementPose(): Pose? {
        return _placementPose.value
    }
    
    /**
     * Manually sets placement pose (for tap-to-place).
     */
    fun setPlacementPose(pose: Pose) {
        _placementPose.value = pose
    }
    
    /**
     * Sets shoe size for scale adjustment.
     */
    fun setShoeSize(size: Float) {
        _shoeSize.value = size
    }
    
    /**
     * Gets scale factor based on shoe size.
     * Uses US shoe sizes as reference.
     */
    fun getScaleFactor(): Float {
        // Base size 9 = scale 1.0
        // Each size up/down adjusts by ~3%
        val baseSize = 9.0f
        val sizeDiff = _shoeSize.value - baseSize
        return 1.0f + (sizeDiff * 0.03f)
    }
    
    /**
     * Checks if floor plane is detected and ready for placement.
     */
    fun isFloorReady(): Boolean {
        return _floorDetected.value && _floorPlane.value != null
    }
    
    /**
     * Checks if foot is detected (for foot tracking mode).
     */
    fun isFootReady(): Boolean {
        return _footDetected.value && _footPose.value != null
    }
    
    /**
     * Checks if placement is ready (any mode).
     */
    fun isPlacementReady(): Boolean {
        return _placementPose.value != null
    }
    
    /**
     * Resets foot tracking state.
     */
    fun reset() {
        _footDetected.value = false
        _footPose.value = null
        _placementPose.value = null
        
        // Keep floor plane if in floor placement mode
        if (_trackingMode.value != FootTrackingMode.FLOOR_PLACEMENT) {
            _floorDetected.value = false
            _floorPlane.value = null
        }
    }
}

/**
 * Foot tracking modes for footwear AR.
 */
enum class FootTrackingMode {
    FLOOR_PLACEMENT,  // Place shoes on detected floor plane
    FOOT_TRACKING     // Track actual foot position (requires ML Kit)
}
