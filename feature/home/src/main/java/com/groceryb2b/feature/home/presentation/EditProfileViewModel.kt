package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditProfileUiState {
    data object Loading : EditProfileUiState()
    data class Success(val shop: ShopEntity) : EditProfileUiState()
    data object Saving : EditProfileUiState()
    data object SaveSuccess : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val shopDao: ShopDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val uiState: StateFlow<EditProfileUiState> = _uiState

    private var currentShop: ShopEntity? = null
    
    private val _shopNameState = MutableStateFlow("")
    val shopNameState: StateFlow<String> = _shopNameState
    
    private val _ownerNameState = MutableStateFlow("")
    val ownerNameState: StateFlow<String> = _ownerNameState
    
    private val _addressState = MutableStateFlow("")
    val addressState: StateFlow<String> = _addressState
    
    private val _deliveryLocationState = MutableStateFlow("")
    val deliveryLocationState: StateFlow<String> = _deliveryLocationState
    
    private val _landmarkState = MutableStateFlow("")
    val landmarkState: StateFlow<String> = _landmarkState
    
    init {
        loadProfileData()
    }
    
    private fun loadProfileData() = viewModelScope.launch {
        try {
            val shop = shopDao.findByMobileNumber(sessionManager.mobileNumber.orEmpty())
            if (shop != null) {
                currentShop = shop
                _shopNameState.value = shop.shopName
                _ownerNameState.value = shop.ownerName
                _addressState.value = shop.address
                _deliveryLocationState.value = shop.deliveryLocation
                _landmarkState.value = shop.landmark.orEmpty()
                _uiState.value = EditProfileUiState.Success(shop)
            } else {
                currentShop = null
                _uiState.value = EditProfileUiState.Error("দোকান খুঁজে পাওয়া যায়নি")
            }
        } catch (e: Exception) {
            _uiState.value = EditProfileUiState.Error(e.message ?: "অজানা ত্রুটি")
        }
    }
    
    fun updateShopName(value: String) {
        _shopNameState.value = value
    }
    
    fun updateOwnerName(value: String) {
        _ownerNameState.value = value
    }
    
    fun updateAddress(value: String) {
        _addressState.value = value
    }
    
    fun updateDeliveryLocation(value: String) {
        _deliveryLocationState.value = value
    }
    
    fun updateLandmark(value: String) {
        _landmarkState.value = value
    }
    
    fun saveChanges() = viewModelScope.launch {
        try {
            val shopToUpdate = currentShop ?: run {
                _uiState.value = EditProfileUiState.Error("দোকানের তথ্য পাওয়া যায়নি")
                return@launch
            }

            _uiState.value = EditProfileUiState.Saving

            val updatedShop = shopToUpdate.copy(
                shopName = _shopNameState.value,
                ownerName = _ownerNameState.value,
                address = _addressState.value,
                deliveryLocation = _deliveryLocationState.value,
                landmark = _landmarkState.value.ifEmpty { null },
                updatedAtEpochMillis = System.currentTimeMillis()
            )

            shopDao.update(updatedShop)
            currentShop = updatedShop
            _uiState.value = EditProfileUiState.SaveSuccess
        } catch (e: Exception) {
            _uiState.value = EditProfileUiState.Error(e.message ?: "সংরক্ষণ ব্যর্থ হয়েছে")
        }
    }
}
