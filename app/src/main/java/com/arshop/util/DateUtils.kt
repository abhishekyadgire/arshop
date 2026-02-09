package com.arshop.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Utility object for date and time operations.
 */
object DateUtils {
    
    /**
     * Formats a Date object to a string with the specified pattern.
     */
    fun formatDate(
        date: Date,
        pattern: String = Constants.DateFormats.DISPLAY_DATE
    ): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Formats a timestamp (milliseconds) to a string.
     */
    fun formatTimestamp(
        timestamp: Long,
        pattern: String = Constants.DateFormats.DISPLAY_DATE
    ): String {
        return formatDate(Date(timestamp), pattern)
    }
    
    /**
     * Formats a Firebase Timestamp to a string.
     */
    fun formatFirebaseTimestamp(
        timestamp: Timestamp,
        pattern: String = Constants.DateFormats.DISPLAY_DATE
    ): String {
        return formatDate(timestamp.toDate(), pattern)
    }
    
    /**
     * Converts a timestamp to relative time (e.g., "2 hours ago").
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                if (hours == 1L) "1 hour ago" else "$hours hours ago"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                if (days == 1L) "Yesterday" else "$days days ago"
            }
            diff < TimeUnit.DAYS.toMillis(30) -> {
                val weeks = TimeUnit.MILLISECONDS.toDays(diff) / 7
                if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
            }
            diff < TimeUnit.DAYS.toMillis(365) -> {
                val months = TimeUnit.MILLISECONDS.toDays(diff) / 30
                if (months == 1L) "1 month ago" else "$months months ago"
            }
            else -> {
                val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
                if (years == 1L) "1 year ago" else "$years years ago"
            }
        }
    }
    
    /**
     * Converts a Firebase Timestamp to relative time.
     */
    fun getRelativeTime(timestamp: Timestamp): String {
        return getRelativeTime(timestamp.toDate().time)
    }
    
    /**
     * Formats an order date for display.
     */
    fun formatOrderDate(timestamp: Long): String {
        return formatTimestamp(timestamp, Constants.DateFormats.DISPLAY_DATE_TIME)
    }
    
    /**
     * Formats an order date from Firebase Timestamp.
     */
    fun formatOrderDate(timestamp: Timestamp): String {
        return formatFirebaseTimestamp(timestamp, Constants.DateFormats.DISPLAY_DATE_TIME)
    }
    
    /**
     * Parses a date string to a Date object.
     */
    fun parseDate(
        dateString: String,
        pattern: String = Constants.DateFormats.DISPLAY_DATE
    ): Date? {
        return try {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Converts Date to Firebase Timestamp.
     */
    fun toFirebaseTimestamp(date: Date): Timestamp {
        return Timestamp(date)
    }
    
    /**
     * Converts milliseconds to Firebase Timestamp.
     */
    fun toFirebaseTimestamp(timestamp: Long): Timestamp {
        return Timestamp(Date(timestamp))
    }
    
    /**
     * Gets the current time as Firebase Timestamp.
     */
    fun now(): Timestamp {
        return Timestamp.now()
    }
    
    /**
     * Checks if a date is today.
     */
    fun isToday(timestamp: Long): Boolean {
        val date = Date(timestamp)
        val today = Date()
        
        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormatter.format(date) == dateFormatter.format(today)
    }
    
    /**
     * Checks if a date is yesterday.
     */
    fun isYesterday(timestamp: Long): Boolean {
        val date = Date(timestamp)
        val yesterday = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))
        
        val dateFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormatter.format(date) == dateFormatter.format(yesterday)
    }
    
    /**
     * Gets the start of day timestamp.
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    /**
     * Gets the end of day timestamp.
     */
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    /**
     * Formats delivery estimate based on order date.
     */
    fun getEstimatedDeliveryDate(orderTimestamp: Long, daysToAdd: Int = 5): String {
        val deliveryDate = Date(orderTimestamp + TimeUnit.DAYS.toMillis(daysToAdd.toLong()))
        return formatDate(deliveryDate, Constants.DateFormats.DISPLAY_DATE)
    }
}
