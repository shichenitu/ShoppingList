package dk.verzier.shoppingv7.data.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.verzier.shoppingv7.data.ItemRepositoryImpl
import dk.verzier.shoppingv7.data.ShopRepositoryImpl
import dk.verzier.shoppingv7.data.UserPreferencesRepositoryImpl
import dk.verzier.shoppingv7.domain.ItemRepository
import dk.verzier.shoppingv7.domain.ShopRepository
import dk.verzier.shoppingv7.domain.UserPreferencesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Singleton
    @Binds
    abstract fun bindShopRepository(impl: ShopRepositoryImpl): ShopRepository

    @Singleton
    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
