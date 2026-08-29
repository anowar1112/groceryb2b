package com.groceryb2b.feature.shopsetup.data

import com.groceryb2b.core.common.Result
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.shopsetup.domain.ShopProfile
import com.groceryb2b.feature.shopsetup.domain.ShopProfileRepository
import android.database.sqlite.SQLiteConstraintException
import javax.inject.Inject

class ShopProfileRepositoryImpl @Inject constructor(
    private val shopDao: ShopDao,
    private val sessionManager: SessionManager
) : ShopProfileRepository {
    override suspend fun createProfile(profile: ShopProfile): Result<String> = try {
        val shopId = shopDao.insert(
            ShopEntity(
                shopName = profile.shopName,
                ownerName = profile.ownerName,
                mobileNumber = profile.mobileNumber,
                address = profile.address,
                deliveryLocation = profile.deliveryLocation,
                landmark = profile.landmark
            )
        )
        sessionManager.shopId = shopId.toString()
        Result.Success(shopId.toString())
    } catch (error: SQLiteConstraintException) {
        Result.Error("এই মোবাইল নম্বর দিয়ে ইতিমধ্যে একটি দোকান নিবন্ধিত আছে", error)
    } catch (error: Exception) {
        Result.Error("অপ্রত্যাশিত সমস্যা হয়েছে", error)
    }
}
