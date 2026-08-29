package com.groceryb2b.feature.shopsetup.domain

data class ShopProfile(
    val shopName: String,
    val ownerName: String,
    val mobileNumber: String,
    val address: String,
    val deliveryLocation: String,
    val landmark: String?
)

interface ShopProfileRepository {
    suspend fun createProfile(profile: ShopProfile): com.groceryb2b.core.common.Result<String>
}
