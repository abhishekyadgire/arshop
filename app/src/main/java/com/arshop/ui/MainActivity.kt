package com.arshop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.arshop.ui.theme.ARShopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ARShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO: Replace with ARShopApp() once navigation is implemented
                    PlaceholderScreen()
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ARShop",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "AR E-Commerce Platform",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.height(32.dp)
                )
                Text(
                    text = "⚠️ Configuration Required",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.height(16.dp)
                )
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.Start,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Before running this app, you need to:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "1. Create a Firebase project")
                    Text(text = "2. Add google-services.json to app/")
                    Text(text = "3. Enable Firebase Authentication")
                    Text(text = "4. Enable Firestore Database")
                    Text(text = "5. Enable Firebase Storage")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "See FIREBASE_SETUP.md for details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.Spacer(modifier: Modifier) {
    androidx.compose.foundation.layout.Spacer(modifier = modifier)
}

@Composable
private fun androidx.compose.foundation.layout.Box(
    modifier: Modifier,
    contentAlignment: androidx.compose.ui.Alignment,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier,
        contentAlignment = contentAlignment,
        content = content
    )
}

@Composable
private fun androidx.compose.ui.Modifier.height(dp: androidx.compose.ui.unit.Dp): Modifier {
    return this.then(androidx.compose.foundation.layout.height(dp))
}

@Composable
private fun androidx.compose.ui.Modifier.padding(
    horizontal: androidx.compose.ui.unit.Dp
): Modifier {
    return this.then(androidx.compose.foundation.layout.padding(horizontal = horizontal))
}

private val dp get() = androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun PlaceholderScreenPreview() {
    ARShopTheme {
        PlaceholderScreen()
    }
}
