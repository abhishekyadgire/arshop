package com.arshop.data.remote

object FirestoreCollections {
    const val USERS = "users"
    const val PRODUCTS = "products"
    const val CART_ITEMS = "cart_items"
    const val ORDERS = "orders"
    const val REVIEWS = "reviews"
    const val ADMIN_USERS = "admin_users"

    // Subcollection paths
    fun userCart(userId: String) = "$USERS/$userId/cart"
    fun userOrders(userId: String) = "$USERS/$userId/orders"
    fun productReviews(productId: String) = "$PRODUCTS/$productId/reviews"
}
