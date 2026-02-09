package com.arshop.ar

import android.app.Activity
import android.content.Context
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages ARCore session lifecycle and configuration.
 * Handles session creation, configuration, pause/resume, and tracking state.
 */
class ARCoreManager(private val context: Context) {
    
    private var arSession: Session? = null
    private var installRequested = false
    
    private val _sessionState = MutableStateFlow(ARSessionState.UNINITIALIZED)
    val sessionState: StateFlow<ARSessionState> = _sessionState.asStateFlow()
    
    private val _trackingState = MutableStateFlow(TrackingState.STOPPED)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Creates and configures ARCore session.
     * @param activity The activity context
     * @param enableDepth Enable depth API if supported
     * @param enableInstantPlacement Enable instant placement for faster AR
     * @return true if session created successfully
     */
    fun createSession(
        activity: Activity,
        enableDepth: Boolean = false,
        enableInstantPlacement: Boolean = true
    ): Boolean {
        try {
            // Check if ARCore is installed and up to date
            when (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    installRequested = true
                    _sessionState.value = ARSessionState.INSTALLING_ARCORE
                    return false
                }
                ArCoreApk.InstallStatus.INSTALLED -> {
                    // ARCore is installed, continue
                }
            }
            
            // Create ARCore session
            arSession = Session(context).apply {
                configure(
                    config.apply {
                        // Enable light estimation
                        lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                        
                        // Enable plane detection
                        planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                        
                        // Enable depth if supported and requested
                        if (enableDepth && isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            depthMode = Config.DepthMode.AUTOMATIC
                        }
                        
                        // Enable instant placement for faster AR experience
                        if (enableInstantPlacement) {
                            instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                        }
                        
                        // Update mode to get latest camera images
                        updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                        
                        // Focus mode
                        focusMode = Config.FocusMode.AUTO
                    }
                )
            }
            
            _sessionState.value = ARSessionState.CREATED
            _error.value = null
            return true
            
        } catch (e: UnavailableArcoreNotInstalledException) {
            _error.value = "ARCore not installed"
            _sessionState.value = ARSessionState.ERROR
        } catch (e: UnavailableApkTooOldException) {
            _error.value = "ARCore APK is too old, please update"
            _sessionState.value = ARSessionState.ERROR
        } catch (e: UnavailableSdkTooOldException) {
            _error.value = "Android SDK is too old"
            _sessionState.value = ARSessionState.ERROR
        } catch (e: UnavailableDeviceNotCompatibleException) {
            _error.value = "Device is not compatible with ARCore"
            _sessionState.value = ARSessionState.ERROR
        } catch (e: Exception) {
            _error.value = "Failed to create AR session: ${e.message}"
            _sessionState.value = ARSessionState.ERROR
        }
        
        return false
    }
    
    /**
     * Resumes the ARCore session.
     */
    fun resume() {
        try {
            arSession?.resume()
            _sessionState.value = ARSessionState.RUNNING
            _error.value = null
        } catch (e: CameraNotAvailableException) {
            _error.value = "Camera not available"
            _sessionState.value = ARSessionState.ERROR
        } catch (e: Exception) {
            _error.value = "Failed to resume AR session: ${e.message}"
            _sessionState.value = ARSessionState.ERROR
        }
    }
    
    /**
     * Pauses the ARCore session.
     */
    fun pause() {
        arSession?.pause()
        _sessionState.value = ARSessionState.PAUSED
    }
    
    /**
     * Destroys the ARCore session and releases resources.
     */
    fun destroy() {
        arSession?.close()
        arSession = null
        _sessionState.value = ARSessionState.DESTROYED
        _trackingState.value = TrackingState.STOPPED
    }
    
    /**
     * Gets the current ARCore session.
     */
    fun getSession(): Session? = arSession
    
    /**
     * Updates tracking state based on current frame.
     */
    fun updateTrackingState(state: TrackingState) {
        _trackingState.value = state
    }
    
    /**
     * Configures camera for AR session.
     */
    fun configureCameraConfig(
        focusMode: Config.FocusMode = Config.FocusMode.AUTO,
        depthMode: Config.DepthMode = Config.DepthMode.DISABLED
    ) {
        arSession?.let { session ->
            session.configure(
                session.config.apply {
                    this.focusMode = focusMode
                    if (session.isDepthModeSupported(depthMode)) {
                        this.depthMode = depthMode
                    }
                }
            )
        }
    }
    
    /**
     * Enables or disables plane detection.
     */
    fun setPlaneDetection(enabled: Boolean) {
        arSession?.let { session ->
            session.configure(
                session.config.apply {
                    planeFindingMode = if (enabled) {
                        Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    } else {
                        Config.PlaneFindingMode.DISABLED
                    }
                }
            )
        }
    }
    
    /**
     * Checks if ARCore is supported on this device.
     */
    fun isARSupported(): Boolean {
        return ArCoreApk.getInstance().checkAvailability(context) 
            == ArCoreApk.Availability.SUPPORTED_INSTALLED
    }
    
    /**
     * Clears error state.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Represents different states of AR session lifecycle.
 */
enum class ARSessionState {
    UNINITIALIZED,
    INSTALLING_ARCORE,
    CREATED,
    RUNNING,
    PAUSED,
    DESTROYED,
    ERROR
}
