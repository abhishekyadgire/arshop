package com.arshop.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductColor(
    val name: String = "",
    val hexValue: String = "#000000"
) : Parcelable {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "hexValue" to hexValue
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): ProductColor {
            return ProductColor(
                name = map["name"] as? String ?: "",
                hexValue = map["hexValue"] as? String ?: "#000000"
            )
        }
    }
}
