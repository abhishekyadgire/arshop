package com.arshop.util

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * String extensions for common validations.
 */
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length >= Constants.Validation.MIN_PASSWORD_LENGTH &&
            this.any { it.isUpperCase() } &&
            this.any { it.isLowerCase() } &&
            this.any { it.isDigit() }
}

fun String.isValidPhone(): Boolean {
    val digitsOnly = this.filter { it.isDigit() }
    return digitsOnly.length == Constants.Validation.PHONE_NUMBER_LENGTH
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

fun String.toSlug(): String {
    return this.lowercase(Locale.getDefault())
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .trim('-')
}

/**
 * Double extensions for price formatting.
 */
fun Double.formatCurrency(locale: Locale = Locale.US): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(this)
}

fun Double.formatPrice(): String {
    return this.formatCurrency()
}

fun Double.calculateTax(rate: Double = Constants.Cart.TAX_RATE): Double {
    return this * rate
}

fun Double.calculateDiscount(percentage: Int): Double {
    return this * (percentage / 100.0)
}

/**
 * Int extensions.
 */
fun Int.formatQuantity(): String {
    return when {
        this > 999 -> "%.1fk".format(this / 1000.0)
        else -> this.toString()
    }
}

/**
 * Long timestamp extensions.
 */
fun Long.toDate(): Date {
    return Date(this)
}

fun Long.formatDate(pattern: String = Constants.DateFormats.DISPLAY_DATE): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000} minutes ago"
        diff < 86400_000 -> "${diff / 3600_000} hours ago"
        diff < 604800_000 -> "${diff / 86400_000} days ago"
        diff < 2592000_000 -> "${diff / 604800_000} weeks ago"
        else -> this.formatDate()
    }
}

/**
 * Date extensions.
 */
fun Date.format(pattern: String = Constants.DateFormats.DISPLAY_DATE): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun Date.toRelativeTime(): String {
    return this.time.toRelativeTime()
}

/**
 * Compose Modifier extensions.
 */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

/**
 * Flow extensions for common operations.
 */
fun <T> Flow<Result<T>>.mapToUiState(): Flow<UiState<T>> {
    return this.map { result ->
        when (result) {
            is Result.Loading -> UiState.Loading
            is Result.Success -> UiState.Success(result.data)
            is Result.Failure -> UiState.Error(
                result.exception.message ?: "An error occurred"
            )
        }
    }.catch { error ->
        emit(UiState.Error(error.message ?: "An unexpected error occurred"))
    }
}

fun <T, R> Flow<Result<T>>.mapResult(transform: (T) -> R): Flow<Result<R>> {
    return this.map { result ->
        when (result) {
            is Result.Loading -> Result.Loading
            is Result.Success -> Result.Success(transform(result.data))
            is Result.Failure -> result
        }
    }
}

fun <T> Flow<Result<T>>.onSuccess(action: suspend (T) -> Unit): Flow<Result<T>> {
    return this.map { result ->
        if (result is Result.Success) {
            action(result.data)
        }
        result
    }
}

fun <T> Flow<Result<T>>.onFailure(action: suspend (Exception) -> Unit): Flow<Result<T>> {
    return this.map { result ->
        if (result is Result.Failure) {
            action(result.exception)
        }
        result
    }
}

/**
 * List extensions.
 */
fun <T> List<T>.chunkedSafely(size: Int): List<List<T>> {
    if (size <= 0) return emptyList()
    return this.chunked(size)
}

fun <T> List<T>.toggle(item: T): List<T> {
    return if (this.contains(item)) {
        this - item
    } else {
        this + item
    }
}

/**
 * UiState sealed class for UI state management.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): String? = (this as? Error)?.message
}
