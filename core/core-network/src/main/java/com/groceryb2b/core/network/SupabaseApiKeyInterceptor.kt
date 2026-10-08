package com.groceryb2b.core.network

import okhttp3.Interceptor
import okhttp3.Response

/** Adds the publishable project key required by Supabase Auth and REST APIs. */
class SupabaseApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
            .build()
        return chain.proceed(request)
    }
}
