package com.arshop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arshop.ui.theme.ARAccentLight
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Product card component for displaying products in lists and grids
 *
 * @param imageUrl Product image URL
 * @param title Product title
 * @param price Current price
 * @param originalPrice Original price (null if no discount)
 * @param rating Product rating (0-5)
 * @param reviewCount Number of reviews
 * @param hasAR Whether AR try-on is available
 * @param isFavorite Whether product is favorited
 * @param onFavoriteClick Callback for favorite button click
 * @param onClick Callback for card click
 * @param modifier Modifier for the card
 */
@Composable
fun ProductCard(
    imageUrl: String,
    title: String,
    price: Double,
    originalPrice: Double? = null,
    rating: Float = 0f,
    reviewCount: Int = 0,
    hasAR: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CustomShapes.ProductCard,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product Image with AR Badge and Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // AR Badge
                if (hasAR) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = ARAccentLight,
                        shape = CustomShapes.Badge
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = "AR Available",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "AR",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                
                // Favorite Button
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Rating
                if (rating > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        RatingBar(
                            rating = rating,
                            modifier = Modifier.height(16.dp)
                        )
                        Text(
                            text = "($reviewCount)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", price)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (originalPrice != null && originalPrice > price) {
                        Text(
                            text = "$${String.format("%.2f", originalPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    ARShopTheme {
        ProductCard(
            imageUrl = "",
            title = "Premium Wireless Headphones",
            price = 199.99,
            originalPrice = 249.99,
            rating = 4.5f,
            reviewCount = 128,
            hasAR = true,
            isFavorite = false,
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
