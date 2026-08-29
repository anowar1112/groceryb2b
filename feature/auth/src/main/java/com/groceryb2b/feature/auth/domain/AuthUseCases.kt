package com.groceryb2b.feature.auth.domain

import com.groceryb2b.core.common.Result
import javax.inject.Inject

private val BD_MOBILE_REGEX = Regex("^01[3-9]\\d{8}$")

class RequestOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(mobileNumber: String): Result<Int> {
        if (!BD_MOBILE_REGEX.matches(mobileNumber)) {
            return Result.Error("সঠিক মোবাইল নম্বর দিন (যেমন 017XXXXXXXX)")
        }
        return repository.requestOtp(mobileNumber)
    }
}

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<OtpVerifyResult> {
        if (otp.length != 6) {
            return Result.Error("৬ সংখ্যার OTP দিন")
        }
        return repository.verifyOtp(mobileNumber, otp)
    }
}
