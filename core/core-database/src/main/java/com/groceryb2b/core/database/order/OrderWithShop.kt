package com.groceryb2b.core.database.order

import androidx.room.Embedded
import androidx.room.Relation
import com.groceryb2b.core.database.shop.ShopEntity

data class OrderWithShop(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "shopId",
        entityColumn = "mobileNumber"
    )
    val shop: ShopEntity?
)
