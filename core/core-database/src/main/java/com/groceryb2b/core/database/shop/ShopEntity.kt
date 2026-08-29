package com.groceryb2b.core.database.shop

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shops",
    indices = [Index(value = ["mobileNumber"], unique = true)]
)
data class ShopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shopName: String,
    val ownerName: String,
    val mobileNumber: String,
    val address: String,
    val deliveryLocation: String,
    val landmark: String?,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)
