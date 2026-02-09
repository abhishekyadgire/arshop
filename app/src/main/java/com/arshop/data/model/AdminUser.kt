package com.arshop.data.model

import com.google.firebase.Timestamp

/**
 * Represents an admin user with elevated privileges.
 */
data class AdminUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "admin",
    val permissions: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val lastLogin: Timestamp? = null
) {
    
    fun hasPermission(permission: String): Boolean {
        return permissions.contains(permission) || permissions.contains("*")
    }
    
    fun canManageProducts(): Boolean {
        return hasPermission("products.manage") || hasPermission("*")
    }
    
    fun canManageOrders(): Boolean {
        return hasPermission("orders.manage") || hasPermission("*")
    }
    
    fun canManageUsers(): Boolean {
        return hasPermission("users.manage") || hasPermission("*")
    }
    
    fun canViewAnalytics(): Boolean {
        return hasPermission("analytics.view") || hasPermission("*")
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "role" to role,
            "permissions" to permissions,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "lastLogin" to lastLogin
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any?>): AdminUser {
            return AdminUser(
                uid = map["uid"] as? String ?: "",
                email = map["email"] as? String ?: "",
                displayName = map["displayName"] as? String ?: "",
                role = map["role"] as? String ?: "admin",
                permissions = (map["permissions"] as? List<*>)?.mapNotNull { 
                    it as? String 
                } ?: emptyList(),
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = map["createdAt"] as? Timestamp ?: Timestamp.now(),
                lastLogin = map["lastLogin"] as? Timestamp
            )
        }
        
        /**
         * Default admin permissions.
         */
        fun getDefaultPermissions(): List<String> {
            return listOf(
                "products.manage",
                "orders.manage",
                "users.view",
                "analytics.view"
            )
        }
        
        /**
         * Super admin permissions (all access).
         */
        fun getSuperAdminPermissions(): List<String> {
            return listOf("*")
        }
    }
}

/**
 * Admin permission constants.
 */
object AdminPermissions {
    const val ALL = "*"
    const val PRODUCTS_MANAGE = "products.manage"
    const val PRODUCTS_VIEW = "products.view"
    const val ORDERS_MANAGE = "orders.manage"
    const val ORDERS_VIEW = "orders.view"
    const val USERS_MANAGE = "users.manage"
    const val USERS_VIEW = "users.view"
    const val ANALYTICS_VIEW = "analytics.view"
    const val SETTINGS_MANAGE = "settings.manage"
}
