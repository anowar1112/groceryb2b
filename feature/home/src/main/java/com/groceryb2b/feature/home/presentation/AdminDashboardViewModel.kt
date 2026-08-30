package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.PermissionManager
import com.groceryb2b.core.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class AdminDashboardUiState(
    val isAdmin: Boolean = false,
    val canAccessAdminPanel: Boolean = false,
    val canCreateProduct: Boolean = false,
    val canUpdateProduct: Boolean = false,
    val canDeleteProduct: Boolean = false
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminDashboardUiState(
            isAdmin = sessionManager.isAdmin,
            canAccessAdminPanel = permissionManager.hasPermission(PermissionAction.ADMIN_PANEL),
            canCreateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_CREATE),
            canUpdateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_UPDATE),
            canDeleteProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_DELETE)
        )
    )

    val uiState: StateFlow<AdminDashboardUiState> = _uiState

    fun refreshPermissions() {
        _uiState.value = AdminDashboardUiState(
            isAdmin = sessionManager.isAdmin,
            canAccessAdminPanel = permissionManager.hasPermission(PermissionAction.ADMIN_PANEL),
            canCreateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_CREATE),
            canUpdateProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_UPDATE),
            canDeleteProduct = permissionManager.hasPermission(PermissionAction.PRODUCT_DELETE)
        )
    }
}
