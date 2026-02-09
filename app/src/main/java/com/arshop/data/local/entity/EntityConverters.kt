package com.arshop.data.local.entity

import com.google.firebase.Timestamp

/**
 * Helper functions for entity conversions.
 */
object EntityConverters {
    
    /**
     * Convert milliseconds to Firebase Timestamp.
     */
    fun longToTimestamp(millis: Long): Timestamp {
        return Timestamp(millis / 1000, ((millis % 1000) * 1000000).toInt())
    }
    
    /**
     * Convert Firebase Timestamp to milliseconds.
     */
    fun timestampToLong(timestamp: Timestamp): Long {
        return timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
    }
}
