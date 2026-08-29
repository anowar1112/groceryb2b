package com.groceryb2b.core.database.catalog

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products", indices = [Index(value = ["categoryId"])])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: String,
    val nameBn: String,
    val nameEn: String,
    val brand: String,
    val unit: String,
    val price: Int,
    val discountPercent: Int = 0,
    val stock: Int,
    val minimumOrderQuantity: Int = 1
)
