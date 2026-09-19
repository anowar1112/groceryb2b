package com.groceryb2b.feature.home.presentation.admin.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.PermissionManager
import com.groceryb2b.core.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPermissionUiState(
    val searchQuery: String = "",
    val shops: List<ShopWithPermissions> = emptyList(),
    val isLoading: Boolean = true,
    val currentUserMobile: String = "",
    val canEditOthers: Boolean = false
)

data class ShopWithPermissions(
    val shop: ShopEntity,
    val permissions: Set<PermissionAction>
)

@HiltViewModel
class AdminPermissionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val permissionManager: PermissionManager,
    private val shopDao: ShopDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<AdminPermissionUiState> = combine(
        shopDao.observeAll(),
        _searchQuery,
        _refreshTrigger
    ) { allShops, query, _ ->
        val queryTrimmed = query.trim()
        val currentUserMobile = sessionManager.mobileNumber.orEmpty()
        
        // Check if current user has ALL permissions
        val allAvailablePermissions = PermissionAction.entries.toSet()
        val currentUserPermissions = mutableSetOf<PermissionAction>()
        PermissionAction.entries.forEach { action ->
            if (permissionManager.hasPermissionForUser(currentUserMobile, action)) {
                currentUserPermissions += action
            }
        }
        val isSuperAdmin = currentUserMobile == "01557775958"
        val hasFullControl = isSuperAdmin || currentUserPermissions.containsAll(allAvailablePermissions)

        val shopsWithPermissions = allShops.map { shop ->
            val permissions = mutableSetOf<PermissionAction>()
            PermissionAction.entries.forEach { action ->
                if (permissionManager.hasPermissionForUser(shop.mobileNumber, action)) {
                    permissions += action
                }
            }
            ShopWithPermissions(shop, permissions)
        }.filter { item ->
            val hasAnyPermission = item.permissions.isNotEmpty() || item.shop.mobileNumber == "01557775958"
            
            if (queryTrimmed.isBlank()) {
                hasAnyPermission
            } else {
                item.shop.shopName.contains(queryTrimmed, ignoreCase = true) || 
                item.shop.mobileNumber.contains(queryTrimmed)
            }
        }

        AdminPermissionUiState(
            searchQuery = query,
            shops = shopsWithPermissions,
            isLoading = false,
            currentUserMobile = currentUserMobile,
            canEditOthers = hasFullControl
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminPermissionUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun togglePermission(mobileNumber: String, action: PermissionAction, shouldBeEnabled: Boolean) {
        val state = uiState.value
        if (!state.canEditOthers) return // Only those with full permissions can edit
        if (mobileNumber == "01557775958") return // Cannot toggle super admin
        if (mobileNumber == state.currentUserMobile) return // Cannot toggle own permissions
        
        viewModelScope.launch {
            if (shouldBeEnabled) {
                permissionManager.grantPermission(mobileNumber, action)
            } else {
                permissionManager.revokePermission(mobileNumber, action)
            }
            _refreshTrigger.value += 1
        }
    }
}
