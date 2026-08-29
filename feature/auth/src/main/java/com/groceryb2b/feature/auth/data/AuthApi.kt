package com.groceryb2b.feature.auth.data

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/otp/request")
    suspend fun requestOtp(@Body body: OtpRequestDto): OtpRequestResponseDto

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body body: OtpVerifyRequestDto): OtpVerifyResponseDto
}
