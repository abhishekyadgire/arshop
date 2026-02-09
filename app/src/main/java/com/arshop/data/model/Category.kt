package com.arshop.data.model

/**
 * Represents a product category.
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val productCount: Int = 0,
    val description: String = "",
    val parentCategory: String? = null
) {
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "imageUrl" to imageUrl,
            "productCount" to productCount,
            "description" to description,
            "parentCategory" to parentCategory
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any?>): Category {
            return Category(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String ?: "",
                productCount = (map["productCount"] as? Number)?.toInt() ?: 0,
                description = map["description"] as? String ?: "",
                parentCategory = map["parentCategory"] as? String
            )
        }
    }
}
