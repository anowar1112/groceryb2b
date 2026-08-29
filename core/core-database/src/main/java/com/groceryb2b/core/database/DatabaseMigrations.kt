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
}
