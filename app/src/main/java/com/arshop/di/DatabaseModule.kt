package com.arshop.di

import android.content.Context
import androidx.room.Room
import com.arshop.data.local.ARShopDatabase
import com.arshop.data.local.dao.CartDao
import com.arshop.data.local.dao.OrderDao
import com.arshop.data.local.dao.ProductDao
import com.arshop.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideARShopDatabase(@ApplicationContext context: Context): ARShopDatabase {
        return Room.databaseBuilder(
            context,
            ARShopDatabase::class.java,
            "arshop_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: ARShopDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: ARShopDatabase): CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(database: ARShopDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: ARShopDatabase): UserDao {
        return database.userDao()
    }
}
