package com.groceryb2b.feature.auth.data

import com.groceryb2b.core.common.Result
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.auth.domain.AuthRepository
import com.groceryb2b.feature.auth.domain.OtpVerifyResult
import javax.inject.Inject

/**
 * Offline MVP authentication. It deliberately does not claim to verify phone ownership.
 * Replace this binding with the API-backed repository before a production release.
 */
class LocalAuthRepository @Inject constructor(
    private val shopDao: ShopDao,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun requestOtp(mobileNumber: String): Result<Int> = Result.Success(0)

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<OtpVerifyResult> {
        if (otp != LOCAL_CONFIRMATION_CODE) {
            return Result.Error("লোকাল মোডে confirmation code $LOCAL_CONFIRMATION_CODE ব্যবহার করুন")
        }
        val shop = shopDao.findByMobileNumber(mobileNumber)
        val adminNumbers = listOf("01557775958", "01700000000")
        sessionManager.accessToken = "local-session-$mobileNumber"
        sessionManager.refreshToken = null
        sessionManager.shopId = shop?.id?.toString()
        sessionManager.mobileNumber = mobileNumber
        sessionManager.isAdmin = mobileNumber in adminNumbers
        if (sessionManager.isAdmin) {
            sessionManager.setUserPermissions(mobileNumber, PermissionAction.values().toSet())
        }
        return Result.Success(
            OtpVerifyResult(
                accessToken = sessionManager.accessToken.orEmpty(),
                refreshToken = "",
                isExistingShop = shop != null,
                shopId = shop?.id?.toString()
            )
        )
    }

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn
    override fun logout() = sessionManager.clear()

    companion object {
        const val LOCAL_CONFIRMATION_CODE = "000000"
    }
}
