package dk.itu.nearesttoilet.data.injection

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.itu.nearesttoilet.data.ToiletRepositoryImpl
import dk.itu.nearesttoilet.domain.ToiletRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindToiletRepository(toiletRepositoryImpl: ToiletRepositoryImpl): ToiletRepository
}