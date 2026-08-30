package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.groceryb2b.core.network.PermissionAction
import com.groceryb2b.core.network.PermissionManager
import com.groceryb2b.core.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AdminPermissionUiState(
    val targetMobile: String = "",
    val grantedPermissions: Set<PermissionAction> = emptySet()
)

@HiltViewModel
class AdminPermissionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPermissionUiState())
    val uiState: StateFlow<AdminPermissionUiState> = _uiState

    fun updateTargetMobile(value: String) {
        _uiState.update {
            it.copy(targetMobile = value.trim())
        }
        refreshGrantedPermissions()
    }

    fun grantPermission(action: PermissionAction) {
        if (!sessionManager.isAdmin) return
        if (_uiState.value.targetMobile.isBlank()) return

        permissionManager.grantPermission(_uiState.value.targetMobile, action)
        refreshGrantedPermissions()
    }

    fun revokePermission(action: PermissionAction) {
        if (!sessionManager.isAdmin) return
        if (_uiState.value.targetMobile.isBlank()) return

        permissionManager.revokePermission(_uiState.value.targetMobile, action)
        refreshGrantedPermissions()
    }

    private fun refreshGrantedPermissions() {
        val target = _uiState.value.targetMobile
        if (target.isBlank()) {
            _uiState.update { it.copy(grantedPermissions = emptySet()) }
            return
        }

        val permissions = permissionManager.hasPermissionForUser(target, PermissionAction.ADMIN_PANEL)
        val granted = mutableSetOf<PermissionAction>()
        PermissionAction.values().forEach { action ->
            if (permissionManager.hasPermissionForUser(target, action)) {
                granted += action
            }
        }
        _uiState.update { it.copy(grantedPermissions = granted) }
    }
}
