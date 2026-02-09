package com.arshop.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Image carousel component for displaying multiple product images
 *
 * @param images List of image URLs
 * @param modifier Modifier for the carousel
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return
    
    val pagerState = rememberPagerState(pageCount = { images.size })
    
    Box(modifier = modifier) {
        // Image Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = "Product image ${page + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(CustomShapes.ImageCarousel),
                contentScale = ContentScale.Crop
            )
        }
        
        // Page Indicators
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                }
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageCarouselPreview() {
    ARShopTheme {
        ImageCarousel(
            images = listOf("", "", "")
        )
    }
}
