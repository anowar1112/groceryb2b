package com.groceryb2b.core.database.order

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "order_items",
    primaryKeys = ["orderId", "productId"],
    indices = [Index(value = ["orderId"])],
    foreignKeys = [
        ForeignKey(entity = OrderEntity::class, parentColumns = ["id"], childColumns = ["orderId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class OrderItemEntity(
    val orderId: Long,
    val productId: Long,
    val productNameBn: String,
    val productNameEn: String,
    val quantity: Int,
    val pricePerUnit: Int,
    val totalPrice: Int
)
