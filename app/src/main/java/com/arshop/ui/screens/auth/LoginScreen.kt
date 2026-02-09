package com.arshop.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arshop.ui.components.LoadingIndicator
import com.arshop.ui.navigation.NavigationActions
import com.arshop.ui.theme.ARShopTheme
import com.arshop.viewmodel.AuthViewModel

/**
 * Login screen for user authentication
 *
 * Features:
 * - Email and password input fields with validation
 * - Login button with loading state
 * - Google Sign-In option
 * - Forgot password link
 * - Sign up link for new users
 * - Error display
 * - Auto-navigate to home on successful login
 *
 * @param navigationActions Navigation actions for screen transitions
 * @param viewModel AuthViewModel for authentication logic
 */
@Composable
fun LoginScreen(
    navigationActions: NavigationActions,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    
    // Navigate to home on successful login
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navigationActions.clearBackStackAndNavigateToHome()
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo/Title
                Text(
                    text = "ARShop",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "AR E-Commerce Platform",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email icon"
                        )
                    },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password icon"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    supportingText = passwordError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (validateInputs(email, password, viewModel) { e, p ->
                                emailError = e
                                passwordError = p
                            }) {
                                viewModel.signIn(email, password)
                            }
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Forgot Password Link
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { navigationActions.navigateToForgotPassword() }
                        .padding(8.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Error Display
                if (uiState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Login Button
                Button(
                    onClick = {
                        if (validateInputs(email, password, viewModel) { e, p ->
                            emailError = e
                            passwordError = p
                        }) {
                            viewModel.signIn(email, password)
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login", style = MaterialTheme.typography.titleMedium)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Google Sign-In Button
                OutlinedButton(
                    onClick = { viewModel.signInWithGoogle() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Sign in with Google", style = MaterialTheme.typography.titleMedium)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Sign Up Link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { navigationActions.navigateToSignUp() }
                    )
                }
            }
        }
    }
}

/**
 * Validates email and password inputs
 *
 * @param email Email to validate
 * @param password Password to validate
 * @param viewModel AuthViewModel for validation methods
 * @param setErrors Callback to set error messages
 * @return True if inputs are valid, false otherwise
 */
private fun validateInputs(
    email: String,
    password: String,
    viewModel: AuthViewModel,
    setErrors: (String?, String?) -> Unit
): Boolean {
    val emailValidation = viewModel.validateEmail(email)
    val passwordValidation = viewModel.validatePassword(password)
    
    setErrors(
        if (!emailValidation.isValid) emailValidation.error else null,
        if (!passwordValidation.isValid) passwordValidation.error else null
    )
    
    return emailValidation.isValid && passwordValidation.isValid
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    ARShopTheme {
        // Preview without actual navigation
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ARShop",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(48.dp))
                OutlinedTextField(
                    value = "user@example.com",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
