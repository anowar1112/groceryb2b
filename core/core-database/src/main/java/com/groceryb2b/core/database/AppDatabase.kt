package com.groceryb2b.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.database.catalog.CategoryDao
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity

@Database(entities = [ShopEntity::class, CategoryEntity::class, ProductEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
}
