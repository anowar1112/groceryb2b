package com.groceryb2b.feature.home.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.PermissionManager
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.home.data.CatalogRepository
import com.groceryb2b.feature.home.data.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AdminDashboardUiState(
    val isAdmin: Boolean = false,
    val canCreateProduct: Boolean = false,
    val canUpdateProduct: Boolean = false,
    val canDeleteProduct: Boolean = false,
    val canManageOrders: Boolean = false,
    val totalProducts: Int = 0,
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val lowStockCount: Int = 0
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val permissionManager: PermissionManager,
    private val catalogRepository: CatalogRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    val uiState: StateFlow<AdminDashboardUiState> = combine(
        catalogRepository.products(),
        orderRepository.observeAllOrders()
    ) { products, orders ->
        AdminDashboardUiState(
            isAdmin = sessionManager.isAdmin,
            canCreateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_CREATE),
            canUpdateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_UPDATE),
            canDeleteProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_DELETE),
            canManageOrders = permissionManager.hasPermission(PermissionAction.ORDER_MANAGE),
            totalProducts = products.size,
            totalOrders = orders.size,
            pendingOrders = orders.count { it.status == "PENDING" },
            lowStockCount = products.count { it.stock < 10 }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AdminDashboardUiState())
}
