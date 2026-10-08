package com.groceryb2b.core.network

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseAuthApi {
    @POST("token")
    suspend fun signInWithPassword(
        @Query("grant_type") grantType: String = "password",
        @Body body: SupabasePasswordRequest
    ): SupabaseSessionDto

    @POST("signup")
    suspend fun signUp(@Body body: SupabasePasswordRequest): SupabaseSessionDto

    @POST("token")
    suspend fun refreshSession(
        @Query("grant_type") grantType: String = "refresh_token",
        @Body body: SupabaseRefreshTokenRequest
    ): SupabaseSessionDto
}

data class SupabasePasswordRequest(val email: String, val password: String)
data class SupabaseRefreshTokenRequest(val refresh_token: String)

data class SupabaseSessionDto(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long
)
