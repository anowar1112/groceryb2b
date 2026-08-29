package com.groceryb2b.feature.auth.data

import com.groceryb2b.core.common.Result
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.auth.domain.AuthRepository
import com.groceryb2b.feature.auth.domain.OtpVerifyResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun requestOtp(mobileNumber: String): Result<Int> = safeCall {
        val response = api.requestOtp(OtpRequestDto(mobileNumber))
        response.resendCooldownSeconds
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<OtpVerifyResult> =
        safeCall {
            val response = api.verifyOtp(OtpVerifyRequestDto(mobileNumber, otp))
            sessionManager.accessToken = response.accessToken
            sessionManager.refreshToken = response.refreshToken
            sessionManager.shopId = response.shopId
            sessionManager.mobileNumber = mobileNumber
            OtpVerifyResult(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                isExistingShop = response.isExistingShop,
                shopId = response.shopId
            )
        }

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn

    override fun logout() = sessionManager.clear()

    /** Centralizes network/HTTP error mapping so every call site returns a clean [Result]. */
    private inline fun <T> safeCall(block: () -> T): Result<T> = try {
        Result.Success(block())
    } catch (e: HttpException) {
        val message = when (e.code()) {
            400 -> "ভুল তথ্য পাঠানো হয়েছে"
            401, 403 -> "OTP সঠিক নয় বা মেয়াদ শেষ হয়ে গেছে"
            429 -> "অনেকবার চেষ্টা করা হয়েছে, একটু পর আবার চেষ্টা করুন"
            in 500..599 -> "সার্ভারে সমস্যা হয়েছে, পরে আবার চেষ্টা করুন"
            else -> "কিছু একটা সমস্যা হয়েছে"
        }
        Result.Error(message, e)
    } catch (e: IOException) {
        Result.Error("ইন্টারনেট সংযোগ পরীক্ষা করুন", e)
    } catch (e: Exception) {
        Result.Error("অপ্রত্যাশিত সমস্যা হয়েছে", e)
    }
}
