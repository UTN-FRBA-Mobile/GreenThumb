package com.utn.greenthumb.client

import com.utn.greenthumb.BuildConfig
import com.utn.greenthumb.client.services.PlantIdApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlantIdApiClient {

    private val authInterceptor = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Api-Key", BuildConfig.PLANT_ID_API_KEY)
            .build()
        chain.proceed(newRequest)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PLANT_ID_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: PlantIdApiService = retrofit.create(PlantIdApiService::class.java)
}
