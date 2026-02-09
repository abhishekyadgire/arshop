package com.arshop.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

/**
 * Utility object for price calculations and formatting.
 */
object PriceUtils {
    
    /**
     * Formats a price value as currency.
     */
    fun formatPrice(
        price: Double,
        locale: Locale = Locale.US,
        currencyCode: String = "USD"
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(locale).apply {
            currency = java.util.Currency.getInstance(currencyCode)
        }
        return formatter.format(price)
    }
    
    /**
     * Formats price with default USD formatting.
     */
    fun formatPriceUSD(price: Double): String {
        return formatPrice(price, Locale.US, "USD")
    }
    
    /**
     * Calculates discount amount from percentage.
     */
    fun calculateDiscount(price: Double, discountPercentage: Int): Double {
        if (discountPercentage < 0 || discountPercentage > 100) return 0.0
        return round(price * discountPercentage / 100.0 * 100) / 100
    }
    
    /**
     * Calculates final price after discount.
     */
    fun calculateDiscountedPrice(price: Double, discountPercentage: Int): Double {
        val discount = calculateDiscount(price, discountPercentage)
        return round((price - discount) * 100) / 100
    }
    
    /**
     * Calculates tax amount.
     */
    fun calculateTax(amount: Double, taxRate: Double = Constants.Cart.TAX_RATE): Double {
        return round(amount * taxRate * 100) / 100
    }
    
    /**
     * Calculates shipping cost based on cart total.
     */
    fun calculateShipping(subtotal: Double): Double {
        return if (subtotal >= Constants.Cart.FREE_SHIPPING_THRESHOLD) {
            0.0
        } else {
            Constants.Cart.STANDARD_SHIPPING_COST
        }
    }
    
    /**
     * Calculates cart total (subtotal + shipping + tax).
     */
    fun calculateTotal(
        subtotal: Double,
        shipping: Double = calculateShipping(subtotal),
        taxRate: Double = Constants.Cart.TAX_RATE
    ): Double {
        val tax = calculateTax(subtotal, taxRate)
        return round((subtotal + shipping + tax) * 100) / 100
    }
    
    /**
     * Calculates order summary.
     */
    fun calculateOrderSummary(
        itemsTotal: Double,
        discountPercentage: Int = 0,
        taxRate: Double = Constants.Cart.TAX_RATE
    ): OrderSummary {
        val discount = if (discountPercentage > 0) {
            calculateDiscount(itemsTotal, discountPercentage)
        } else {
            0.0
        }
        
        val subtotal = itemsTotal - discount
        val shipping = calculateShipping(subtotal)
        val tax = calculateTax(subtotal, taxRate)
        val total = subtotal + shipping + tax
        
        return OrderSummary(
            itemsTotal = itemsTotal,
            discount = discount,
            subtotal = subtotal,
            shipping = shipping,
            tax = tax,
            total = total
        )
    }
    
    /**
     * Formats percentage discount.
     */
    fun formatDiscount(discountPercentage: Int): String {
        return "$discountPercentage% OFF"
    }
    
    /**
     * Calculates savings amount.
     */
    fun calculateSavings(originalPrice: Double, discountedPrice: Double): Double {
        return round((originalPrice - discountedPrice) * 100) / 100
    }
    
    /**
     * Calculates savings percentage.
     */
    fun calculateSavingsPercentage(originalPrice: Double, discountedPrice: Double): Int {
        if (originalPrice <= 0) return 0
        val savings = calculateSavings(originalPrice, discountedPrice)
        return ((savings / originalPrice) * 100).toInt()
    }
    
    /**
     * Rounds price to 2 decimal places.
     */
    fun roundPrice(price: Double): Double {
        return round(price * 100) / 100
    }
    
    /**
     * Checks if free shipping is applicable.
     */
    fun isFreeShippingEligible(subtotal: Double): Boolean {
        return subtotal >= Constants.Cart.FREE_SHIPPING_THRESHOLD
    }
    
    /**
     * Calculates amount needed for free shipping.
     */
    fun amountNeededForFreeShipping(subtotal: Double): Double {
        val needed = Constants.Cart.FREE_SHIPPING_THRESHOLD - subtotal
        return if (needed > 0) roundPrice(needed) else 0.0
    }
    
    /**
     * Validates price value.
     */
    fun isValidPrice(price: Double): Boolean {
        return price >= Constants.Product.MIN_PRICE && 
               price <= Constants.Product.MAX_PRICE &&
               price == roundPrice(price)
    }
    
    /**
     * Formats price range.
     */
    fun formatPriceRange(minPrice: Double, maxPrice: Double): String {
        return "${formatPriceUSD(minPrice)} - ${formatPriceUSD(maxPrice)}"
    }
}

/**
 * Data class representing order summary calculations.
 */
data class OrderSummary(
    val itemsTotal: Double,
    val discount: Double,
    val subtotal: Double,
    val shipping: Double,
    val tax: Double,
    val total: Double
) {
    fun getFormattedItemsTotal(): String = PriceUtils.formatPriceUSD(itemsTotal)
    fun getFormattedDiscount(): String = PriceUtils.formatPriceUSD(discount)
    fun getFormattedSubtotal(): String = PriceUtils.formatPriceUSD(subtotal)
    fun getFormattedShipping(): String = 
        if (shipping == 0.0) "FREE" else PriceUtils.formatPriceUSD(shipping)
    fun getFormattedTax(): String = PriceUtils.formatPriceUSD(tax)
    fun getFormattedTotal(): String = PriceUtils.formatPriceUSD(total)
}
