package com.groceryb2b.feature.auth.data

import com.groceryb2b.core.common.Result
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.core.network.SupabaseShopApi
import com.groceryb2b.core.network.CreateRemoteShopDto
import com.groceryb2b.core.network.SupabaseAuthApi
import com.groceryb2b.core.network.SupabasePasswordRequest
import com.groceryb2b.core.network.SupabaseSessionRefresher
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.feature.auth.domain.AuthRepository
import com.groceryb2b.feature.auth.domain.OtpVerifyResult
import com.groceryb2b.feature.auth.BuildConfig
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: SupabaseAuthApi,
    private val sessionManager: SessionManager,
    private val sessionRefresher: SupabaseSessionRefresher,
    private val shopDao: ShopDao,
    private val shopApi: SupabaseShopApi
) : AuthRepository {

    override suspend fun requestOtp(mobileNumber: String): Result<Int> {
        if (!BuildConfig.DEBUG) {
            return Result.Error("বাস্তব OTP পরিষেবা কনফিগার করা নেই")
        }
        return if (mobileNumber.isBangladeshiMobileNumber()) {
            // Test mode deliberately keeps the current screen flow and does not send SMS.
            Result.Success(0)
        } else {
            Result.Error("সঠিক ১১ সংখ্যার মোবাইল নম্বর দিন")
        }
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<OtpVerifyResult> =
        safeCall {
            check(BuildConfig.DEBUG) { "টেস্ট OTP শুধু debug build-এ ব্যবহার করা যাবে" }
            require(mobileNumber.isBangladeshiMobileNumber()) { "সঠিক মোবাইল নম্বর দিন" }
            require(otp == TEST_CONFIRMATION_CODE) { "টেস্ট কোড $TEST_CONFIRMATION_CODE ব্যবহার করুন" }

            val credentials = SupabasePasswordRequest(
                email = "$mobileNumber@$TEST_EMAIL_DOMAIN",
                password = TEST_CONFIRMATION_CODE
            )
            val session = try {
                authApi.signInWithPassword(body = credentials)
            } catch (error: HttpException) {
                if (error.code() !in listOf(400, 401)) throw error
                authApi.signUp(credentials)
            }

            sessionRefresher.saveSession(session)
            sessionManager.mobileNumber = mobileNumber
            sessionManager.shopId = null

            val remoteShop = shopApi.findByMobileNumber("eq.$mobileNumber").firstOrNull()
            val existingLocalShop = shopDao.findByMobileNumber(mobileNumber)
            val localShop = if (remoteShop != null) {
                existingLocalShop ?: ShopEntity(
                    shopName = remoteShop.shopName,
                    ownerName = remoteShop.ownerName,
                    mobileNumber = remoteShop.mobileNumber,
                    address = remoteShop.address,
                    deliveryLocation = remoteShop.deliveryLocation,
                    landmark = remoteShop.landmark
                ).let { shop ->
                    val localId = shopDao.insert(shop)
                    shop.copy(id = localId)
                }
            } else if (existingLocalShop != null) {
                // Move a pre-Supabase profile to the server during its first login.
                shopApi.create(
                    CreateRemoteShopDto(
                        shopName = existingLocalShop.shopName,
                        ownerName = existingLocalShop.ownerName,
                        mobileNumber = existingLocalShop.mobileNumber,
                        address = existingLocalShop.address,
                        deliveryLocation = existingLocalShop.deliveryLocation,
                        landmark = existingLocalShop.landmark
                    )
                )
                existingLocalShop
            } else {
                null
            }
            sessionManager.shopId = localShop?.id?.toString()
            
            // Define admin numbers here
            val adminNumbers = listOf("01557775958", "01700000000")
            sessionManager.isAdmin = mobileNumber in adminNumbers
            if (sessionManager.isAdmin) {
                sessionManager.setUserPermissions(mobileNumber, PermissionAction.values().toSet())
            }

            OtpVerifyResult(
                accessToken = session.access_token,
                refreshToken = session.refresh_token,
                isExistingShop = localShop != null,
                shopId = sessionManager.shopId
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
        Result.Error(e.message ?: "অপ্রত্যাশিত সমস্যা হয়েছে", e)
    }

    private fun String.isBangladeshiMobileNumber(): Boolean =
        length == 11 && startsWith("01") && all(Char::isDigit)

    private companion object {
        const val TEST_CONFIRMATION_CODE = "000000"
        const val TEST_EMAIL_DOMAIN = "groceryb2b.test"
    }
}
