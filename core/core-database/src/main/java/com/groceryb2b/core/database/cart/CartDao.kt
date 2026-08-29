package com.groceryb2b.core.database.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE shopId = :shopId") fun observeByShop(shopId: String): Flow<List<CartItemEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: CartItemEntity)
    @Query("DELETE FROM cart_items WHERE shopId = :shopId AND productId = :productId") suspend fun delete(shopId: String, productId: Long)
    @Query("DELETE FROM cart_items WHERE shopId = :shopId") suspend fun clear(shopId: String)
}
