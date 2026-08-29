package com.groceryb2b.core.database.catalog

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val nameBn: String,
    val nameEn: String,
    val sortOrder: Int
)
