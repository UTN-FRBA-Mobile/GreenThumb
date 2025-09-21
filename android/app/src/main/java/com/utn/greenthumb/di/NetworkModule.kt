package com.utn.greenthumb.di

import com.utn.greenthumb.client.PlantIdApiClient
import com.utn.greenthumb.client.services.PlantIdApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePlantIdApiService(): PlantIdApiService {
        return PlantIdApiClient.api
    }
}
