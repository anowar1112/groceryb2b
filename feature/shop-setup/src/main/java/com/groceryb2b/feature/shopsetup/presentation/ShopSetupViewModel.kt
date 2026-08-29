package com.groceryb2b.feature.shopsetup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.common.Result
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.shopsetup.domain.ShopProfile
import com.groceryb2b.feature.shopsetup.domain.ShopProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val BD_MOBILE_REGEX = Regex("^01[3-9]\\d{8}$")

data class ShopSetupUiState(
    val shopName: String = "",
    val ownerName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val deliveryLocation: String = "",
    val landmark: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
)

@HiltViewModel
class ShopSetupViewModel @Inject constructor(
    private val repository: ShopProfileRepository,
    sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopSetupUiState(mobileNumber = sessionManager.mobileNumber.orEmpty()))
    val uiState: StateFlow<ShopSetupUiState> = _uiState.asStateFlow()

    fun update(transform: (ShopSetupUiState) -> ShopSetupUiState) = _uiState.update(transform)

    fun save() {
        val state = _uiState.value
        val requiredFields = listOf(state.shopName, state.ownerName, state.mobileNumber, state.address, state.deliveryLocation)
        if (requiredFields.any { it.isBlank() }) {
            _uiState.update { it.copy(errorMessage = "* চিহ্নিত সব তথ্য দিন") }
            return
        }
        if (!BD_MOBILE_REGEX.matches(state.mobileNumber)) {
            _uiState.update { it.copy(errorMessage = "সঠিক মোবাইল নম্বর দিন") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = repository.createProfile(state.toProfile())) {
                is Result.Success -> _uiState.update { it.copy(isSaving = false, isCompleted = true) }
                is Result.Error -> _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                Result.Loading -> Unit
            }
        }
    }
}

private fun ShopSetupUiState.toProfile() = ShopProfile(
    shopName = shopName.trim(), ownerName = ownerName.trim(), mobileNumber = mobileNumber,
    address = address.trim(), deliveryLocation = deliveryLocation.trim(), landmark = landmark.trim().ifBlank { null }
)
