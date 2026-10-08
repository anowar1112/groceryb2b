package com.groceryb2b.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseSessionRefresher @Inject constructor(
    private val authApi: SupabaseAuthApi,
    private val sessionManager: SessionManager
) {
    suspend fun ensureFreshSession() {
        if (!sessionManager.shouldRefreshAccessToken) return
        val refreshToken = checkNotNull(sessionManager.refreshToken) {
            "Session is expired and has no refresh token. Please sign in again."
        }
        val session = authApi.refreshSession(body = SupabaseRefreshTokenRequest(refreshToken))
        sessionManager.accessToken = session.access_token
        sessionManager.refreshToken = session.refresh_token
        sessionManager.accessTokenExpiresAtEpochMillis =
            System.currentTimeMillis() + session.expires_in * 1_000L
    }

    fun saveSession(session: SupabaseSessionDto) {
        sessionManager.accessToken = session.access_token
        sessionManager.refreshToken = session.refresh_token
        sessionManager.accessTokenExpiresAtEpochMillis =
            System.currentTimeMillis() + session.expires_in * 1_000L
    }
}
