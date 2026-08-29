package com.groceryb2b.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.database.catalog.CategoryDao
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity
import com.groceryb2b.core.database.cart.CartDao
import com.groceryb2b.core.database.cart.CartItemEntity
import com.groceryb2b.core.database.order.OrderDao
import com.groceryb2b.core.database.order.OrderEntity
import com.groceryb2b.core.database.order.OrderItemEntity

@Database(entities = [ShopEntity::class, CategoryEntity::class, ProductEntity::class, CartItemEntity::class, OrderEntity::class, OrderItemEntity::class], version = 4, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}
