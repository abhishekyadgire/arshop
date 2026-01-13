package com.arshop.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "", // "Clothing" or "Footwear"
    val gender: String = "", // "Men", "Women", "Unisex"
    val subcategory: String = "", // e.g., "Shirts", "Sneakers"
    val images: List<String> = emptyList(),
    val model3dUrl: String? = null,
    val isArEnabled: Boolean = false,
    val sizes: List<String> = emptyList(),
    val colors: List<ProductColor> = emptyList(),
    val stock: Map<String, Int> = emptyMap(), // key: "size-color", value: quantity
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) {

    fun isInStock(size: String, color: String): Boolean {
        val key = "$size-$color"
        return (stock[key] ?: 0) > 0
    }

    fun getStockCount(size: String, color: String): Int {
        val key = "$size-$color"
        return stock[key] ?: 0
    }

    fun hasStock(): Boolean {
        return stock.values.any { it > 0 }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "price" to price,
            "category" to category,
            "gender" to gender,
            "subcategory" to subcategory,
            "images" to images,
            "model3dUrl" to model3dUrl,
            "isArEnabled" to isArEnabled,
            "sizes" to sizes,
            "colors" to colors.map { it.toMap() },
            "stock" to stock,
            "rating" to rating,
            "reviewCount" to reviewCount,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        fun fromFirestore(document: DocumentSnapshot): Product? {
            return try {
                Product(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    price = document.getDouble("price") ?: 0.0,
                    category = document.getString("category") ?: "",
                    gender = document.getString("gender") ?: "",
                    subcategory = document.getString("subcategory") ?: "",
                    images = document.get("images") as? List<String> ?: emptyList(),
                    model3dUrl = document.getString("model3dUrl"),
                    isArEnabled = document.getBoolean("isArEnabled") ?: false,
                    sizes = document.get("sizes") as? List<String> ?: emptyList(),
                    colors = (document.get("colors") as? List<*>)?.mapNotNull { colorMap ->
                        (colorMap as? Map<*, *>)?.let { map ->
                            @Suppress("UNCHECKED_CAST")
                            ProductColor.fromMap(map as Map<String, Any?>)
                        }
                    } ?: emptyList(),
                    stock = (document.get("stock") as? Map<*, *>)?.mapNotNull { entry ->
                        val key = entry.key as? String
                        val value = (entry.value as? Number)?.toInt()
                        if (key != null && value != null) key to value else null
                    }?.toMap() ?: emptyMap(),
                    rating = document.getDouble("rating")?.toFloat() ?: 0f,
                    reviewCount = (document.getLong("reviewCount") ?: 0).toInt(),
                    createdAt = document.getTimestamp("createdAt") ?: Timestamp.now(),
                    updatedAt = document.getTimestamp("updatedAt") ?: Timestamp.now()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
