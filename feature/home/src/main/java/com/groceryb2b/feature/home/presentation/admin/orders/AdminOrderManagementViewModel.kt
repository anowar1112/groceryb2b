package com.groceryb2b.feature.home.presentation.admin.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.order.OrderWithShop
import com.groceryb2b.feature.home.data.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminOrderUiState(
    val orders: List<OrderWithShop> = emptyList(),
    val isLoading: Boolean = true,
    val selectedStatus: String? = null
)

@HiltViewModel
class AdminOrderManagementViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val selectedStatus = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminOrderUiState> = combine(
        orderRepository.observeAllOrdersWithShop(),
        selectedStatus
    ) { orders, status ->
        val filteredOrders = if (status == null) orders else orders.filter { it.order.status == status }
        
        AdminOrderUiState(
            orders = filteredOrders,
            isLoading = false,
            selectedStatus = status
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminOrderUiState())

    fun updateFilter(status: String?) {
        selectedStatus.value = status
    }

    fun updateOrderStatus(orderId: Long, newStatus: String) = viewModelScope.launch {
        orderRepository.updateOrderStatus(orderId, newStatus)
    }
}
