package com.arshop.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for AR functionality.
 * Manages AR session state, model loading, and screenshot capture.
 */
@HiltViewModel
class ARViewModel @Inject constructor() : ViewModel() {
    
    private val _arEnabled = MutableStateFlow(false)
    val arEnabled: StateFlow<Boolean> = _arEnabled.asStateFlow()
    
    private val _modelLoaded = MutableStateFlow(false)
    val modelLoaded: StateFlow<Boolean> = _modelLoaded.asStateFlow()
    
    private val _trackingState = MutableStateFlow(TrackingState.INITIALIZING)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()
    
    private val _screenshot = MutableStateFlow<Bitmap?>(null)
    val screenshot: StateFlow<Bitmap?> = _screenshot.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _modelUrl = MutableStateFlow<String?>(null)
    val modelUrl: StateFlow<String?> = _modelUrl.asStateFlow()
    
    private val _showPlacementGuide = MutableStateFlow(true)
    val showPlacementGuide: StateFlow<Boolean> = _showPlacementGuide.asStateFlow()
    
    /**
     * Initializes AR session.
     */
    fun initARSession() {
        viewModelScope.launch {
            try {
                _arEnabled.value = true
                _trackingState.value = TrackingState.INITIALIZING
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to initialize AR session"
                _arEnabled.value = false
            }
        }
    }
    
    /**
     * Loads 3D model for AR.
     */
    fun loadModel(modelUrl: String) {
        viewModelScope.launch {
            try {
                _modelUrl.value = modelUrl
                _modelLoaded.value = false
                _error.value = null
                
                // Simulate model loading
                // In real implementation, this would load the actual 3D model
                // using ARCore Scene Viewer or Sceneform
                
                _modelLoaded.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load 3D model"
                _modelLoaded.value = false
            }
        }
    }
    
    /**
     * Updates tracking state.
     */
    fun updateTrackingState(state: TrackingState) {
        _trackingState.value = state
        
        // Hide placement guide once tracking is good
        if (state == TrackingState.TRACKING) {
            _showPlacementGuide.value = false
        }
    }
    
    /**
     * Captures AR screenshot.
     */
    fun captureScreenshot(bitmap: Bitmap) {
        viewModelScope.launch {
            _screenshot.value = bitmap
        }
    }
    
    /**
     * Clears captured screenshot.
     */
    fun clearScreenshot() {
        _screenshot.value = null
    }
    
    /**
     * Resets AR session.
     */
    fun resetARSession() {
        _arEnabled.value = false
        _modelLoaded.value = false
        _trackingState.value = TrackingState.INITIALIZING
        _screenshot.value = null
        _modelUrl.value = null
        _showPlacementGuide.value = true
        _error.value = null
    }
    
    /**
     * Toggles placement guide visibility.
     */
    fun togglePlacementGuide() {
        _showPlacementGuide.value = !_showPlacementGuide.value
    }
    
    /**
     * Checks if AR is ready for use.
     */
    fun isARReady(): Boolean {
        return _arEnabled.value && 
               _modelLoaded.value && 
               _trackingState.value == TrackingState.TRACKING
    }
    
    /**
     * Clears error message.
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * Represents AR tracking states.
 */
enum class TrackingState {
    INITIALIZING,
    INSUFFICIENT_LIGHT,
    INSUFFICIENT_FEATURES,
    TRACKING,
    PAUSED,
    STOPPED
}
