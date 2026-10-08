package com.groceryb2b.core.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager =
        SessionManager(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor =
        AuthInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideSupabaseApiKeyInterceptor(): SupabaseApiKeyInterceptor = SupabaseApiKeyInterceptor()

    @Provides
    @Singleton
    fun providePermissionManager(sessionManager: SessionManager): PermissionManager =
        DefaultPermissionManager(sessionManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // NOTE: set to NONE in release builds via build-type config.
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("supabaseAuth")
    fun provideSupabaseAuthRetrofit(
        authInterceptor: AuthInterceptor,
        apiKeyInterceptor: SupabaseApiKeyInterceptor
    ): Retrofit {
        require(BuildConfig.SUPABASE_URL.isNotBlank()) {
            "Set SUPABASE_URL in local.properties before building the app."
        }
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.SUPABASE_URL}/auth/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(apiKeyInterceptor)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthApi(@Named("supabaseAuth") retrofit: Retrofit): SupabaseAuthApi =
        retrofit.create(SupabaseAuthApi::class.java)

    @Provides
    @Singleton
    @Named("supabaseRest")
    fun provideSupabaseRestRetrofit(
        authInterceptor: AuthInterceptor,
        apiKeyInterceptor: SupabaseApiKeyInterceptor
    ): Retrofit = Retrofit.Builder()
        .baseUrl("${BuildConfig.SUPABASE_URL}/rest/v1/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideSupabaseShopApi(@Named("supabaseRest") retrofit: Retrofit): SupabaseShopApi =
        retrofit.create(SupabaseShopApi::class.java)

    @Provides
    @Singleton
    fun provideSupabaseOrderApi(@Named("supabaseRest") retrofit: Retrofit): SupabaseOrderApi =
        retrofit.create(SupabaseOrderApi::class.java)
}
