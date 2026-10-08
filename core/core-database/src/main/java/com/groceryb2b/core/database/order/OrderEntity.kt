package com.groceryb2b.core.database.order

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["shopId"]),
        Index(value = ["remoteId"], unique = true),
        Index(value = ["clientSyncId"], unique = true)
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String? = null,
    val clientSyncId: String? = null,
    val shopId: String,
    val totalPrice: Int,
    val status: String, // PENDING, CONFIRMED, DELIVERED, CANCELLED
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val updatedAtEpochMillis: Long = System.currentTimeMillis()
)
