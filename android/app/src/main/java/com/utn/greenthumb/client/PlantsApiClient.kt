package com.utn.greenthumb.client

import com.utn.greenthumb.BuildConfig
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlantsApiClient {

    lateinit var authRepository: AuthRepository

    fun init(authRepository: AuthRepository) {
        this.authRepository = authRepository
    }

    private val authInterceptor = Interceptor { chain ->
        val userId = authRepository.getCurrentUser()?.uid ?: ""
        val newRequest = chain.request().newBuilder()
            .addHeader("x-client-id", userId)
            .build()
        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PLANT_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: PlantsApiService = retrofit.create(PlantsApiService::class.java)
}
