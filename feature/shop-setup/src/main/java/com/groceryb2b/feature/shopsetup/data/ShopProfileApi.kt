package com.groceryb2b.feature.shopsetup.data

import retrofit2.http.Body
import retrofit2.http.POST

interface ShopProfileApi {
    @POST("shops/me/profile")
    suspend fun createProfile(@Body body: CreateShopProfileDto): CreateShopProfileResponseDto
}

data class CreateShopProfileDto(
    val shopName: String,
    val ownerName: String,
    val mobileNumber: String,
    val address: String,
    val deliveryLocation: String,
    val landmark: String?
)

data class CreateShopProfileResponseDto(val shopId: String)
