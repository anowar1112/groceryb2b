package com.groceryb2b.core.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseOrderApi {
    @POST("orders")
    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    suspend fun upsertOrder(
        @Query("on_conflict") conflictColumn: String = "client_id",
        @Body order: CreateRemoteOrderDto
    ): List<RemoteOrderDto>

    @POST("order_items")
    @Headers("Prefer: resolution=merge-duplicates,return=minimal")
    suspend fun upsertOrderItems(
        @Query("on_conflict") conflictColumn: String = "client_id",
        @Body items: List<CreateRemoteOrderItemDto>
    ): Unit

    @GET("orders")
    suspend fun ordersForShop(
        @Query("shop_id") shopIdFilter: String,
        @Query("select") select: String = "*,order_items(*)",
        @Query("order") order: String = "created_at.desc"
    ): List<RemoteOrderDto>

    @PATCH("orders")
    @Headers("Prefer: return=minimal")
    suspend fun updateOrderStatus(
        @Query("id") orderIdFilter: String,
        @Body status: UpdateRemoteOrderStatusDto
    ): Unit
}

data class CreateRemoteOrderDto(
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("total_price") val totalPrice: Int,
    val status: String
)

data class CreateRemoteOrderItemDto(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("product_name_bn") val productNameBn: String,
    @SerializedName("product_name_en") val productNameEn: String,
    val quantity: Int,
    @SerializedName("price_per_unit") val pricePerUnit: Int,
    @SerializedName("total_price") val totalPrice: Int
)

data class UpdateRemoteOrderStatusDto(val status: String)

data class RemoteOrderDto(
    val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("total_price") val totalPrice: Int,
    val status: String,
    @SerializedName("client_id") val clientId: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("order_items") val items: List<RemoteOrderItemDto> = emptyList()
)

data class RemoteOrderItemDto(
    val id: String,
    @SerializedName("product_name_bn") val productNameBn: String,
    @SerializedName("product_name_en") val productNameEn: String,
    val quantity: Int,
    @SerializedName("price_per_unit") val pricePerUnit: Int,
    @SerializedName("total_price") val totalPrice: Int
)
