package com.groceryb2b.core.database

import android.content.Context
import androidx.room.Room
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.catalog.CategoryDao
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.cart.CartDao
import com.groceryb2b.core.database.order.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "grocery_b2b.db")
            .addMigrations(DatabaseMigrations.MIGRATION_1_2, DatabaseMigrations.MIGRATION_2_3, DatabaseMigrations.MIGRATION_3_4)
            .build()

    @Provides
    fun provideShopDao(database: AppDatabase): ShopDao = database.shopDao()

    @Provides fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
    @Provides fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()
    @Provides fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
    @Provides fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()
}
