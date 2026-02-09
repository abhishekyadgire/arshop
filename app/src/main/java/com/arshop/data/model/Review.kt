package com.arshop.data.model

import com.google.firebase.Timestamp

/**
 * Represents a product review.
 */
data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val rating: Float = 0f,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val helpful: Int = 0,
    val verified: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
) {
    
    fun isValid(): Boolean {
        return productId.isNotBlank() && 
               userId.isNotBlank() && 
               rating in 0f..5f
    }
    
    fun getFormattedDate(): String {
        return com.arshop.util.DateUtils.getRelativeTime(timestamp)
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "productId" to productId,
            "userId" to userId,
            "userName" to userName,
            "userPhotoUrl" to userPhotoUrl,
            "rating" to rating,
            "comment" to comment,
            "images" to images,
            "helpful" to helpful,
            "verified" to verified,
            "timestamp" to timestamp
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any?>): Review {
            return Review(
                id = map["id"] as? String ?: "",
                productId = map["productId"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                userPhotoUrl = map["userPhotoUrl"] as? String,
                rating = (map["rating"] as? Number)?.toFloat() ?: 0f,
                comment = map["comment"] as? String ?: "",
                images = (map["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                helpful = (map["helpful"] as? Number)?.toInt() ?: 0,
                verified = map["verified"] as? Boolean ?: false,
                timestamp = map["timestamp"] as? Timestamp ?: Timestamp.now()
            )
        }
    }
}

/**
 * Review statistics for a product.
 */
data class ReviewStats(
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val fiveStarCount: Int = 0,
    val fourStarCount: Int = 0,
    val threeStarCount: Int = 0,
    val twoStarCount: Int = 0,
    val oneStarCount: Int = 0
) {
    
    fun getFiveStarPercentage(): Float {
        return if (totalReviews > 0) (fiveStarCount.toFloat() / totalReviews) * 100 else 0f
    }
    
    fun getFourStarPercentage(): Float {
        return if (totalReviews > 0) (fourStarCount.toFloat() / totalReviews) * 100 else 0f
    }
    
    fun getThreeStarPercentage(): Float {
        return if (totalReviews > 0) (threeStarCount.toFloat() / totalReviews) * 100 else 0f
    }
    
    fun getTwoStarPercentage(): Float {
        return if (totalReviews > 0) (twoStarCount.toFloat() / totalReviews) * 100 else 0f
    }
    
    fun getOneStarPercentage(): Float {
        return if (totalReviews > 0) (oneStarCount.toFloat() / totalReviews) * 100 else 0f
    }
}
