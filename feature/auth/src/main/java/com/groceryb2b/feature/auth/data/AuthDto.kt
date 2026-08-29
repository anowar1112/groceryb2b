package com.groceryb2b.feature.auth.data

data class OtpRequestDto(val mobileNumber: String)

data class OtpRequestResponseDto(
    val success: Boolean,
    val resendCooldownSeconds: Int
)

data class OtpVerifyRequestDto(val mobileNumber: String, val otp: String)

data class OtpVerifyResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val isExistingShop: Boolean,
    val shopId: String?
)
