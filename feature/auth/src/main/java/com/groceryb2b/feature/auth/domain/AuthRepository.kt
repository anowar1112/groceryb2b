package com.groceryb2b.feature.auth.domain

import com.groceryb2b.core.common.Result

interface AuthRepository {
    /** Sends an OTP to [mobileNumber]. Returns the resend-cooldown in seconds on success. */
    suspend fun requestOtp(mobileNumber: String): Result<Int>

    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<OtpVerifyResult>

    fun isLoggedIn(): Boolean

    fun logout()
}
