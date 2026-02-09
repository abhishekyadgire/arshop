package com.arshop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arshop.ui.theme.ARShopTheme

/**
 * Address form component for entering shipping/billing addresses
 *
 * @param name Full name
 * @param onNameChange Callback when name changes
 * @param street Street address
 * @param onStreetChange Callback when street changes
 * @param city City
 * @param onCityChange Callback when city changes
 * @param state State/Province
 * @param onStateChange Callback when state changes
 * @param zipCode ZIP/Postal code
 * @param onZipCodeChange Callback when zip changes
 * @param country Country
 * @param onCountryChange Callback when country changes
 * @param enabled Whether form is enabled
 * @param modifier Modifier for the form
 */
@Composable
fun AddressForm(
    name: String,
    onNameChange: (String) -> Unit,
    street: String,
    onStreetChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    zipCode: String,
    onZipCodeChange: (String) -> Unit,
    country: String,
    onCountryChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        
        OutlinedTextField(
            value = street,
            onValueChange = onStreetChange,
            label = { Text("Street Address") },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            OutlinedTextField(
                value = state,
                onValueChange = onStateChange,
                label = { Text("State") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = zipCode,
                onValueChange = onZipCodeChange,
                label = { Text("ZIP Code") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            
            OutlinedTextField(
                value = country,
                onValueChange = onCountryChange,
                label = { Text("Country") },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressFormPreview() {
    ARShopTheme {
        AddressForm(
            name = "",
            onNameChange = {},
            street = "",
            onStreetChange = {},
            city = "",
            onCityChange = {},
            state = "",
            onStateChange = {},
            zipCode = "",
            onZipCodeChange = {},
            country = "",
            onCountryChange = {}
        )
    }
}
