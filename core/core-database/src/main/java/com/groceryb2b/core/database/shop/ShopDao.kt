package com.groceryb2b.core.database.shop

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops WHERE mobileNumber = :mobileNumber LIMIT 1")
    suspend fun findByMobileNumber(mobileNumber: String): ShopEntity?

    @Query("SELECT * FROM shops WHERE mobileNumber = :mobileNumber LIMIT 1")
    fun observeByMobileNumber(mobileNumber: String): Flow<ShopEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(shop: ShopEntity): Long

    @Update
    suspend fun update(shop: ShopEntity)
}
