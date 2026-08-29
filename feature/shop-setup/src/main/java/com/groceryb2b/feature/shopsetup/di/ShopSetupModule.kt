package com.groceryb2b.feature.shopsetup.di

import com.groceryb2b.feature.shopsetup.data.ShopProfileRepositoryImpl
import com.groceryb2b.feature.shopsetup.domain.ShopProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShopSetupRepositoryModule {
    @Binds @Singleton
    abstract fun bindShopProfileRepository(impl: ShopProfileRepositoryImpl): ShopProfileRepository
}
