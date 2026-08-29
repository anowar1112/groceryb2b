package com.groceryb2b.core.database.shop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops WHERE mobileNumber = :mobileNumber LIMIT 1")
    suspend fun findByMobileNumber(mobileNumber: String): ShopEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(shop: ShopEntity): Long
}
