package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.feature.home.data.OrderRepository
import com.groceryb2b.feature.home.data.OrderSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrderDetailsUiState {
    data object Loading : OrderDetailsUiState()
    data class Success(val order: OrderSummary) : OrderDetailsUiState()
    data class Error(val message: String) : OrderDetailsUiState()
}

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrderDetailsUiState>(OrderDetailsUiState.Loading)
    val uiState: StateFlow<OrderDetailsUiState> = _uiState
    
    fun loadOrder(orderId: Long) = viewModelScope.launch {
        try {
            _uiState.value = OrderDetailsUiState.Loading
            val order = orderRepository.getOrderById(orderId)
            
            if (order != null) {
                _uiState.value = OrderDetailsUiState.Success(order)
            } else {
                _uiState.value = OrderDetailsUiState.Error("অর্ডার পাওয়া যায়নি")
            }
        } catch (e: Exception) {
            _uiState.value = OrderDetailsUiState.Error(e.message ?: "অজানা ত্রুটি")
        }
    }
}
