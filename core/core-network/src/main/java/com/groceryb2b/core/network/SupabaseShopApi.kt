package com.groceryb2b.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseShopApi {
    @GET("shops")
    suspend fun allShops(
        @Query("select") select: String = "*"
    ): List<RemoteShopDto>

    @GET("shops")
    suspend fun findByMobileNumber(
        @Query("mobile_number") mobileNumberFilter: String,
        @Query("select") select: String = "*"
    ): List<RemoteShopDto>

    @POST("shops")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body shop: CreateRemoteShopDto): List<RemoteShopDto>

    @PATCH("shops")
    @Headers("Prefer: return=minimal")
    suspend fun update(
        @Query("id") shopIdFilter: String,
        @Body shop: UpdateRemoteShopDto
    )
}

data class UpdateRemoteShopDto(
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("owner_name") val ownerName: String,
    val address: String,
    @SerializedName("delivery_location") val deliveryLocation: String,
    val landmark: String?
)

data class CreateRemoteShopDto(
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("mobile_number") val mobileNumber: String,
    val address: String,
    @SerializedName("delivery_location") val deliveryLocation: String,
    val landmark: String?
)

data class RemoteShopDto(
    val id: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("mobile_number") val mobileNumber: String,
    val address: String,
    @SerializedName("delivery_location") val deliveryLocation: String,
    val landmark: String?
)
