package com.groceryb2b.core.database

import android.content.Context
import androidx.room.Room
import com.groceryb2b.core.database.shop.ShopDao
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
            .build()

    @Provides
    fun provideShopDao(database: AppDatabase): ShopDao = database.shopDao()
}
