package com.arshop.util

import android.util.Patterns

/**
 * Utility object for input validation.
 */
object ValidationUtils {
    
    /**
     * Validates email address format.
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult.Error("Please enter a valid email address")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates password strength.
     * Requirements: min 8 chars, uppercase, lowercase, digit
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Password is required")
            password.length < Constants.Validation.MIN_PASSWORD_LENGTH -> 
                ValidationResult.Error("Password must be at least ${Constants.Validation.MIN_PASSWORD_LENGTH} characters")
            password.length > Constants.Validation.MAX_PASSWORD_LENGTH -> 
                ValidationResult.Error("Password must be less than ${Constants.Validation.MAX_PASSWORD_LENGTH} characters")
            !password.any { it.isUpperCase() } -> 
                ValidationResult.Error("Password must contain at least one uppercase letter")
            !password.any { it.isLowerCase() } -> 
                ValidationResult.Error("Password must contain at least one lowercase letter")
            !password.any { it.isDigit() } -> 
                ValidationResult.Error("Password must contain at least one digit")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates phone number format (US format).
     */
    fun validatePhoneNumber(phone: String): ValidationResult {
        val digitsOnly = phone.filter { it.isDigit() }
        return when {
            phone.isBlank() -> ValidationResult.Error("Phone number is required")
            digitsOnly.length != Constants.Validation.PHONE_NUMBER_LENGTH -> 
                ValidationResult.Error("Phone number must be ${Constants.Validation.PHONE_NUMBER_LENGTH} digits")
            !Patterns.PHONE.matcher(phone).matches() -> 
                ValidationResult.Error("Please enter a valid phone number")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates name (first name, last name, full name).
     */
    fun validateName(name: String, fieldName: String = "Name"): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("$fieldName is required")
            name.length < Constants.Validation.MIN_NAME_LENGTH -> 
                ValidationResult.Error("$fieldName must be at least ${Constants.Validation.MIN_NAME_LENGTH} characters")
            name.length > Constants.Validation.MAX_NAME_LENGTH -> 
                ValidationResult.Error("$fieldName must be less than ${Constants.Validation.MAX_NAME_LENGTH} characters")
            !name.matches(Regex("^[a-zA-Z\\s]+\$")) -> 
                ValidationResult.Error("$fieldName can only contain letters and spaces")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates address line.
     */
    fun validateAddress(address: String, fieldName: String = "Address"): ValidationResult {
        return when {
            address.isBlank() -> ValidationResult.Error("$fieldName is required")
            address.length < 5 -> ValidationResult.Error("$fieldName must be at least 5 characters")
            address.length > 100 -> ValidationResult.Error("$fieldName must be less than 100 characters")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates city name.
     */
    fun validateCity(city: String): ValidationResult {
        return when {
            city.isBlank() -> ValidationResult.Error("City is required")
            city.length < 2 -> ValidationResult.Error("City name must be at least 2 characters")
            !city.matches(Regex("^[a-zA-Z\\s]+\$")) -> 
                ValidationResult.Error("City name can only contain letters and spaces")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates state/province.
     */
    fun validateState(state: String): ValidationResult {
        return when {
            state.isBlank() -> ValidationResult.Error("State is required")
            state.length < 2 -> ValidationResult.Error("State must be at least 2 characters")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates ZIP/postal code.
     */
    fun validateZipCode(zipCode: String): ValidationResult {
        val digitsOnly = zipCode.filter { it.isDigit() }
        return when {
            zipCode.isBlank() -> ValidationResult.Error("ZIP code is required")
            digitsOnly.length != Constants.Validation.ZIP_CODE_LENGTH -> 
                ValidationResult.Error("ZIP code must be ${Constants.Validation.ZIP_CODE_LENGTH} digits")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates credit card number using Luhn algorithm.
     */
    fun validateCardNumber(cardNumber: String): ValidationResult {
        val digitsOnly = cardNumber.filter { it.isDigit() }
        
        return when {
            cardNumber.isBlank() -> ValidationResult.Error("Card number is required")
            digitsOnly.length < 13 || digitsOnly.length > 19 -> 
                ValidationResult.Error("Card number must be between 13 and 19 digits")
            !isValidLuhn(digitsOnly) -> ValidationResult.Error("Invalid card number")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates card CVV.
     */
    fun validateCvv(cvv: String): ValidationResult {
        return when {
            cvv.isBlank() -> ValidationResult.Error("CVV is required")
            cvv.length < 3 || cvv.length > 4 -> 
                ValidationResult.Error("CVV must be 3 or 4 digits")
            !cvv.all { it.isDigit() } -> ValidationResult.Error("CVV must contain only digits")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Validates card expiry date (MM/YY format).
     */
    fun validateExpiryDate(expiry: String): ValidationResult {
        val parts = expiry.split("/")
        if (parts.size != 2) {
            return ValidationResult.Error("Expiry date must be in MM/YY format")
        }
        
        val month = parts[0].toIntOrNull()
        val year = parts[1].toIntOrNull()
        
        return when {
            month == null || year == null -> 
                ValidationResult.Error("Invalid expiry date")
            month < 1 || month > 12 -> 
                ValidationResult.Error("Invalid month")
            else -> ValidationResult.Success
        }
    }
    
    /**
     * Luhn algorithm for credit card validation.
     */
    private fun isValidLuhn(number: String): Boolean {
        var sum = 0
        var alternate = false
        
        for (i in number.length - 1 downTo 0) {
            var digit = number[i].digitToInt()
            
            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            
            sum += digit
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
}

/**
 * Result of a validation operation.
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
    
    val isValid: Boolean
        get() = this is Success
    
    val errorMessage: String?
        get() = (this as? Error)?.message
}
