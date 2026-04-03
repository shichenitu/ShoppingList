package dk.itu.nearesttoilet.data.injection

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dk.itu.nearesttoilet.data.remote.CopenhagenApiService
import dk.itu.nearesttoilet.data.remote.FrederiksbergApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CphRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FrbRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true; isLenient = true }
    }

    @Provides
    @Singleton
    @CphRetrofit
    fun provideCopenhagenRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://wfs-kbhkort.kk.dk/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @FrbRetrofit
    fun provideFrederiksbergRetrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gc2ekstern.frederiksberg.dk/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideCopenhagenApiService(
        @CphRetrofit retrofit: Retrofit
    ): CopenhagenApiService {
        return retrofit.create(CopenhagenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFrederiksbergApiService(
        @FrbRetrofit retrofit: Retrofit
    ): FrederiksbergApiService {
        return retrofit.create(FrederiksbergApiService::class.java)
    }
}