package com.groceryb2b.core.database.cart

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "cart_items", primaryKeys = ["shopId", "productId"], indices = [Index(value = ["shopId"])])
data class CartItemEntity(val shopId: String, val productId: Long, val quantity: Int)
