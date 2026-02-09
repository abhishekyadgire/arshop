package com.arshop.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Utility object for network connectivity operations.
 */
object NetworkUtils {
    
    /**
     * Checks if the device is connected to the internet.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? ConnectivityManager ?: return false
        
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Checks if connected via WiFi.
     */
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? ConnectivityManager ?: return false
        
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    
    /**
     * Checks if connected via cellular data.
     */
    fun isCellularConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? ConnectivityManager ?: return false
        
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
    
    /**
     * Gets the current network connection type.
     */
    fun getConnectionType(context: Context): ConnectionType {
        if (!isNetworkAvailable(context)) {
            return ConnectionType.NONE
        }
        
        return when {
            isWifiConnected(context) -> ConnectionType.WIFI
            isCellularConnected(context) -> ConnectionType.CELLULAR
            else -> ConnectionType.OTHER
        }
    }
    
    /**
     * Observes network connectivity changes as a Flow.
     */
    fun observeNetworkConnectivity(context: Context): Flow<NetworkState> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as ConnectivityManager
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkState.Available)
            }
            
            override fun onLost(network: Network) {
                trySend(NetworkState.Unavailable)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                val isConnected = capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) && capabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                
                trySend(if (isConnected) NetworkState.Available else NetworkState.Unavailable)
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // Send initial state
        trySend(
            if (isNetworkAvailable(context)) NetworkState.Available 
            else NetworkState.Unavailable
        )
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
    
    /**
     * Checks if the connection is metered (limited data).
     */
    fun isConnectionMetered(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as? ConnectivityManager ?: return false
        
        return connectivityManager.isActiveNetworkMetered
    }
}

/**
 * Network connection types.
 */
enum class ConnectionType {
    NONE,
    WIFI,
    CELLULAR,
    OTHER
}

/**
 * Network availability states.
 */
sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
    
    val isAvailable: Boolean
        get() = this is Available
}

/**
 * Context extension to check network availability.
 */
fun Context.isNetworkAvailable(): Boolean {
    return NetworkUtils.isNetworkAvailable(this)
}

/**
 * Context extension to observe network connectivity.
 */
fun Context.observeNetworkConnectivity(): Flow<NetworkState> {
    return NetworkUtils.observeNetworkConnectivity(this)
}
