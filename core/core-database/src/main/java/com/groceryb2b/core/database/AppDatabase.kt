package com.groceryb2b.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity

@Database(entities = [ShopEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}
