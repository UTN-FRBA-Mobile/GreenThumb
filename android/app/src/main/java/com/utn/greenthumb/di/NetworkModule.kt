package com.utn.greenthumb.di

import com.utn.greenthumb.client.PlantsApiClient
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.repository.AuthRepository
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
    fun providePlantIdApiService(authRepository: AuthRepository): PlantsApiService {
        PlantsApiClient.init(authRepository)
        return PlantsApiClient.api
    }

}
