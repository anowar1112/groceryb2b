package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.home.data.CartRepository
import com.groceryb2b.feature.home.data.CartSummary
import com.groceryb2b.feature.home.data.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CheckoutUiState {
    data object Loading : CheckoutUiState()
    data class Success(val cart: CartSummary, val orderId: Long? = null) : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val shopId = sessionManager.shopId ?: sessionManager.mobileNumber.orEmpty()
    
    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState
    
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    
    // Convert Flow to StateFlow so we can access current value
    val cartSummary: StateFlow<CartSummary> = cartRepository.observeCartByShop(shopId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartSummary())
    
    init {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Success(CartSummary())
        }
    }
    
    fun placeOrder() = viewModelScope.launch {
        try {
            _isProcessing.value = true
            
            // ✅ FIX: Now correctly get the StateFlow's current value
            val currentCart = cartSummary.value
            
            if (currentCart.items.isEmpty()) {
                _uiState.value = CheckoutUiState.Error("কার্ট খালি। অনুগ্রহ করে পণ্য যোগ করুন।")
                _isProcessing.value = false
                return@launch
            }
            
            val orderId = orderRepository.createOrder(shopId, currentCart.items)
            
            // Clear the cart after successful order creation
            cartRepository.clearCart(shopId)
            
            _uiState.value = CheckoutUiState.Success(CartSummary(), orderId = orderId)
            _isProcessing.value = false
        } catch (e: Exception) {
            _uiState.value = CheckoutUiState.Error(e.message ?: "অর্ডার তৈরিতে ত্রুটি হয়েছে")
            _isProcessing.value = false
        }
    }
    
    fun updateQuantity(productId: Long, quantity: Int) = viewModelScope.launch {
        cartRepository.updateQuantity(shopId, productId, quantity)
    }
    
    fun clearCart() = viewModelScope.launch {
        cartRepository.clearCart(shopId)
    }
}
