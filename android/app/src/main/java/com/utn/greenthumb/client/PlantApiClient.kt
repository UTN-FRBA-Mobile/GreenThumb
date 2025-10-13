package com.utn.greenthumb.client

import com.utn.greenthumb.client.services.PlantApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlantApiClient {
    private const val BASE_URL = "http://10.0.2.2:4000/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: PlantApiService = retrofit.create(PlantApiService::class.java)
}
