package com.arshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arshop.data.model.Product
import com.arshop.data.model.ProductColor
import com.google.firebase.Timestamp

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val gender: String,
    val subcategory: String,
    val images: List<String>,
    val model3dUrl: String?,
    val isArEnabled: Boolean,
    val sizes: List<String>,
    val colorsJson: String, // Stored as JSON string
    val stock: Map<String, Int>,
    val rating: Float,
    val reviewCount: Int,
    val cachedAt: Long = System.currentTimeMillis()
) {
    fun toProduct(): Product {
        val colors = try {
            com.google.gson.Gson().fromJson(
                colorsJson,
                Array<ProductColor>::class.java
            ).toList()
        } catch (e: Exception) {
            emptyList()
        }

        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            category = category,
            gender = gender,
            subcategory = subcategory,
            images = images,
            model3dUrl = model3dUrl,
            isArEnabled = isArEnabled,
            sizes = sizes,
            colors = colors,
            stock = stock,
            rating = rating,
            reviewCount = reviewCount
        )
    }

    companion object {
        fun fromProduct(product: Product): ProductEntity {
            val colorsJson = com.google.gson.Gson().toJson(product.colors)
            return ProductEntity(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category,
                gender = product.gender,
                subcategory = product.subcategory,
                images = product.images,
                model3dUrl = product.model3dUrl,
                isArEnabled = product.isArEnabled,
                sizes = product.sizes,
                colorsJson = colorsJson,
                stock = product.stock,
                rating = product.rating,
                reviewCount = product.reviewCount
            )
        }
    }
}
