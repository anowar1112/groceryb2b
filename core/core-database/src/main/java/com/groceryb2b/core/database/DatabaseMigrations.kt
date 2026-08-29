package com.groceryb2b.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `nameBn` TEXT NOT NULL, `nameEn` TEXT NOT NULL, `sortOrder` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` TEXT NOT NULL, `nameBn` TEXT NOT NULL, `nameEn` TEXT NOT NULL, `brand` TEXT NOT NULL, `unit` TEXT NOT NULL, `price` INTEGER NOT NULL, `discountPercent` INTEGER NOT NULL, `stock` INTEGER NOT NULL, `minimumOrderQuantity` INTEGER NOT NULL)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_products_categoryId` ON `products` (`categoryId`)")
        }
    }
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `cart_items` (`shopId` TEXT NOT NULL, `productId` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, PRIMARY KEY(`shopId`, `productId`))")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_cart_items_shopId` ON `cart_items` (`shopId`)")
        }
    }
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `shopId` TEXT NOT NULL, `totalPrice` INTEGER NOT NULL, `status` TEXT NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `updatedAtEpochMillis` INTEGER NOT NULL)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_orders_shopId` ON `orders` (`shopId`)")
            database.execSQL("CREATE TABLE IF NOT EXISTS `order_items` (`orderId` INTEGER NOT NULL, `productId` INTEGER NOT NULL, `productNameBn` TEXT NOT NULL, `productNameEn` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `pricePerUnit` INTEGER NOT NULL, `totalPrice` INTEGER NOT NULL, PRIMARY KEY(`orderId`, `productId`), FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) ON DELETE CASCADE)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_orderId` ON `order_items` (`orderId`)")
        }
    }
}
