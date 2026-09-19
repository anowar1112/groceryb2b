package com.groceryb2b.core.network

import javax.inject.Inject
import javax.inject.Singleton

enum class PermissionAction {
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,
    ORDER_MANAGE
}

interface PermissionManager {
    fun isCurrentUserAdmin(): Boolean
    fun hasPermission(action: PermissionAction): Boolean
    fun hasPermissionForUser(mobileNumber: String, action: PermissionAction): Boolean
    fun grantPermission(targetMobileNumber: String, action: PermissionAction): Boolean
    fun revokePermission(targetMobileNumber: String, action: PermissionAction): Boolean
}

@Singleton
class DefaultPermissionManager @Inject constructor(
    private val sessionManager: SessionManager
) : PermissionManager {

    override fun isCurrentUserAdmin(): Boolean = sessionManager.isAdmin

    override fun hasPermission(action: PermissionAction): Boolean {
        return sessionManager.isAdmin || action in sessionManager.userPermissions
    }

    override fun hasPermissionForUser(
        mobileNumber: String,
        action: PermissionAction
    ): Boolean {
        if (mobileNumber == "01557775958") return true // Super Admin always has all
        if (mobileNumber == sessionManager.mobileNumber && sessionManager.isAdmin) {
            return true
        }
        return action in sessionManager.getPermissionsForUser(mobileNumber)
    }

    override fun grantPermission(targetMobileNumber: String, action: PermissionAction): Boolean {
        if (!sessionManager.isAdmin) return false
        val permissions = sessionManager.getPermissionsForUser(targetMobileNumber).toMutableSet()
        permissions += action
        sessionManager.setUserPermissions(targetMobileNumber, permissions)
        return true
    }

    override fun revokePermission(targetMobileNumber: String, action: PermissionAction): Boolean {
        if (!sessionManager.isAdmin) return false
        val permissions = sessionManager.getPermissionsForUser(targetMobileNumber).toMutableSet()
        permissions -= action
        sessionManager.setUserPermissions(targetMobileNumber, permissions)
        return true
    }
}
