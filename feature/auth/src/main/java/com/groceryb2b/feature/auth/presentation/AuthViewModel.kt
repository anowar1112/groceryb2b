package com.groceryb2b.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.common.Result
import com.groceryb2b.feature.auth.domain.RequestOtpUseCase
import com.groceryb2b.feature.auth.domain.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val requestOtpUseCase: RequestOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var cooldownJob: kotlinx.coroutines.Job? = null

    fun onMobileNumberChanged(value: String) {
        // Keep only digits, cap at 11 (Bangladeshi mobile length).
        val digitsOnly = value.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(mobileNumber = digitsOnly, errorMessage = null) }
    }

    fun onOtpChanged(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otp = digitsOnly, errorMessage = null) }
    }

    fun requestOtp() {
        val mobile = _uiState.value.mobileNumber
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = requestOtpUseCase(mobile)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = AuthStep.OTP_ENTRY,
                            resendCooldownSeconds = result.data
                        )
                    }
                    startCooldown(result.data)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun resendOtp() {
        if (_uiState.value.resendCooldownSeconds > 0) return
        requestOtp()
    }

    fun verifyOtp() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = verifyOtpUseCase(state.mobileNumber, state.otp)) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigateToShopSetup = !result.data.isExistingShop,
                        navigateToHome = result.data.isExistingShop
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun onBackToMobileEntry() {
        cooldownJob?.cancel()
        _uiState.update {
            it.copy(step = AuthStep.MOBILE_ENTRY, otp = "", errorMessage = null)
        }
    }

    private fun startCooldown(seconds: Int) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.update { it.copy(resendCooldownSeconds = remaining) }
            }
        }
    }

    override fun onCleared() {
        cooldownJob?.cancel()
        super.onCleared()
    }
}
