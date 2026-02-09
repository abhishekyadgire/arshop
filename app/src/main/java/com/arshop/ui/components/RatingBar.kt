package com.arshop.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.StarYellow
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Rating bar component displaying star ratings
 *
 * @param rating Rating value (0-5)
 * @param maxRating Maximum rating (default: 5)
 * @param showValue Whether to show numeric value
 * @param compact Whether to use compact mode
 * @param tint Color for filled stars
 * @param modifier Modifier for the rating bar
 */
@Composable
fun RatingBar(
    rating: Float,
    maxRating: Int = 5,
    showValue: Boolean = false,
    compact: Boolean = true,
    tint: Color = StarYellow,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fullStars = floor(rating).toInt()
        val hasHalfStar = rating - fullStars >= 0.5f
        val emptyStars = maxRating - ceil(rating).toInt()
        
        // Full stars
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(if (compact) 16.dp else 24.dp)
            )
        }
        
        // Half star
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Filled.StarHalf,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(if (compact) 16.dp else 24.dp)
            )
        }
        
        // Empty stars
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(if (compact) 16.dp else 24.dp)
            )
        }
        
        // Rating value
        if (showValue) {
            Text(
                text = String.format("%.1f", rating),
                style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingBarPreview() {
    ARShopTheme {
        RatingBar(
            rating = 4.5f,
            showValue = true,
            compact = false
        )
    }
}
