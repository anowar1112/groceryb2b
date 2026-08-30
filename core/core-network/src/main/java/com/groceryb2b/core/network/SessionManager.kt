package com.groceryb2b.core.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the current auth token in memory + persists it in
 * EncryptedSharedPreferences-backed storage (swap the delegate for
 * EncryptedSharedPreferences in production; plain prefs kept here for
 * step-1 simplicity).
 */
@Singleton
class SessionManager @Inject constructor(
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("grocery_b2b_session", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_ACCESS_TOKEN, value) }

    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_REFRESH_TOKEN, value) }

    var shopId: String?
        get() = prefs.getString(KEY_SHOP_ID, null)
        set(value) = prefs.edit { putString(KEY_SHOP_ID, value) }

    var mobileNumber: String?
        get() = prefs.getString(KEY_MOBILE_NUMBER, null)
        set(value) = prefs.edit { putString(KEY_MOBILE_NUMBER, value) }

    var isAdmin: Boolean
        get() = prefs.getBoolean(KEY_IS_ADMIN, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_ADMIN, value) }

    var userPermissions: Set<PermissionAction>
        get() = prefs.getStringSet(KEY_USER_PERMISSIONS, emptySet())
            ?.mapNotNull { runCatching { PermissionAction.valueOf(it) }.getOrNull() }
            ?.toSet()
            ?: emptySet()
        set(value) = prefs.edit { putStringSet(KEY_USER_PERMISSIONS, value.map { it.name }.toSet()) }

    val isLoggedIn: Boolean
        get() = !accessToken.isNullOrBlank()

    fun setUserPermissions(mobileNumber: String, permissions: Set<PermissionAction>) {
        val key = "user_permissions_$mobileNumber"
        prefs.edit { putStringSet(key, permissions.map { it.name }.toSet()) }
    }

    fun getPermissionsForUser(mobileNumber: String): Set<PermissionAction> {
        val key = "user_permissions_$mobileNumber"
        return prefs.getStringSet(key, emptySet())
            ?.mapNotNull { runCatching { PermissionAction.valueOf(it) }.getOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    fun clear() = prefs.edit { clear() }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_SHOP_ID = "shop_id"
        const val KEY_MOBILE_NUMBER = "mobile_number"
        const val KEY_IS_ADMIN = "is_admin"
        const val KEY_USER_PERMISSIONS = "user_permissions"
    }
}
