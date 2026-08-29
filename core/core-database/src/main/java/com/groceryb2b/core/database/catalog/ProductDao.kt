package com.groceryb2b.core.database.catalog

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY nameBn")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM products WHERE nameEn = :nameEn)")
    suspend fun existsByNameEn(nameEn: String): Boolean

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Insert
    suspend fun insert(product: ProductEntity): Long
}
