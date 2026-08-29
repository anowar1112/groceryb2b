package com.groceryb2b.feature.auth.domain

/**
 * Whether this mobile number belongs to an existing shop (→ go straight
 * to Home after OTP) or a new one (→ go to Shop Setup after OTP).
 */
data class OtpVerifyResult(
    val accessToken: String,
    val refreshToken: String,
    val isExistingShop: Boolean,
    val shopId: String?
)
