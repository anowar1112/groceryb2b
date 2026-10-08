package com.groceryb2b.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches the access token to every outgoing request except the OTP
 * request/verify endpoints (which happen before a token exists).
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        // Supabase Auth endpoints authenticate with the publishable key and must
        // not inherit a stale session from a previous test user.
        val isPublicEndpoint = path.contains("/auth/")
        if (isPublicEndpoint) return chain.proceed(original)

        val token = sessionManager.accessToken
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }
}
