package com.groceryb2b.feature.auth.presentation

/** Which screen of the auth flow is currently shown. */
enum class AuthStep { MOBILE_ENTRY, OTP_ENTRY }

data class AuthUiState(
    val step: AuthStep = AuthStep.MOBILE_ENTRY,
    val mobileNumber: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resendCooldownSeconds: Int = 0,
    val navigateToShopSetup: Boolean = false,
    val navigateToHome: Boolean = false
)
