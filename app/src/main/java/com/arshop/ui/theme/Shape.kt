package com.arshop.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Shape definitions for ARShop application
 */
val Shapes = Shapes(
    // Extra Small - Used for chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - Used for small cards, text fields
    small = RoundedCornerShape(8.dp),
    
    // Medium - Used for cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large - Used for bottom sheets, large cards
    large = RoundedCornerShape(16.dp),
    
    // Extra Large - Used for bottom sheets, modals
    extraLarge = RoundedCornerShape(28.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val ProductCard = RoundedCornerShape(16.dp)
    val CategoryChip = RoundedCornerShape(20.dp)
    val FilterChip = RoundedCornerShape(8.dp)
    val BottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val SearchBar = RoundedCornerShape(24.dp)
    val ImageCarousel = RoundedCornerShape(12.dp)
    val Badge = RoundedCornerShape(12.dp)
    val QuantitySelector = RoundedCornerShape(8.dp)
    val SizeChip = RoundedCornerShape(8.dp)
    val ColorCircle = RoundedCornerShape(50) // Circular
    val ARButton = RoundedCornerShape(12.dp)
    val AddToCartButton = RoundedCornerShape(12.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val BottomNavigation = RoundedCornerShape(0.dp) // No rounding
    val TopRoundedCard = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val FullyRounded = RoundedCornerShape(50) // Circular/pill shape
}
