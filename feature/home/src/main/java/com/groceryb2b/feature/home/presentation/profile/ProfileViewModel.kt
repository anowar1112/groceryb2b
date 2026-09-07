package com.groceryb2b.feature.home.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.home.data.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.groceryb2b.core.database.order.OrderEntity
import javax.inject.Inject

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val shop: ShopEntity,
        val orders: List<OrderEntity> = emptyList()
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val shopDao: ShopDao,
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val shopId = sessionManager.shopId ?: sessionManager.mobileNumber.orEmpty()
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState
    
    init {
        loadProfileData()
    }
    
    private fun loadProfileData() = viewModelScope.launch {
        try {
            val shop = shopDao.findByMobileNumber(sessionManager.mobileNumber.orEmpty())
                ?: return@launch
            
            // Collect orders from flow
            orderRepository.observeOrdersByShop(shopId).collect { orders ->
                _uiState.value = ProfileUiState.Success(shop, orders)
            }
        } catch (e: Exception) {
            _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
        }
    }
}
