package com.arshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arshop.data.local.dao.CartDao
import com.arshop.data.local.dao.OrderDao
import com.arshop.data.local.dao.ProductDao
import com.arshop.data.local.dao.UserDao
import com.arshop.data.local.entity.CartItemEntity
import com.arshop.data.local.entity.OrderEntity
import com.arshop.data.local.entity.ProductEntity
import com.arshop.data.local.entity.UserEntity

/**
 * ARShop Room Database.
 * 
 * Provides local caching for offline-first data access.
 * Includes entities for products, cart items, orders, and users.
 */
@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ARShopDatabase : RoomDatabase() {
    
    /**
     * DAO for product operations.
     */
    abstract fun productDao(): ProductDao
    
    /**
     * DAO for cart operations.
     */
    abstract fun cartDao(): CartDao
    
    /**
     * DAO for order operations.
     */
    abstract fun orderDao(): OrderDao
    
    /**
     * DAO for user operations.
     */
    abstract fun userDao(): UserDao
}
