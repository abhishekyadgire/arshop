package com.arshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme
import com.arshop.ui.theme.CustomShapes

/**
 * Search bar component
 *
 * @param query Current search query
 * @param onQueryChange Callback when query changes
 * @param onSearch Callback when search is submitted
 * @param onClear Callback when clear button is clicked
 * @param placeholder Placeholder text
 * @param enabled Whether the search bar is enabled
 * @param modifier Modifier for the search bar
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit = { onQueryChange("") },
    placeholder: String = "Search products...",
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        } else null,
        singleLine = true,
        enabled = enabled,
        shape = CustomShapes.SearchBar,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(query) }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    ARShopTheme {
        SearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}
