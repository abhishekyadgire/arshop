package com.arshop.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderStatusUpdate(
    val status: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val note: String? = null
) : Parcelable {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "status" to status,
            "timestamp" to timestamp,
            "note" to note
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): OrderStatusUpdate {
            return OrderStatusUpdate(
                status = map["status"] as? String ?: "",
                timestamp = map["timestamp"] as? Timestamp ?: Timestamp.now(),
                note = map["note"] as? String
            )
        }
    }
}
