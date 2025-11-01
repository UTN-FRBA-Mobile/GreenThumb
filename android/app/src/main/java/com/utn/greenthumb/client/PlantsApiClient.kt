package com.utn.greenthumb.client

import com.google.gson.GsonBuilder
import com.utn.greenthumb.BuildConfig
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.repository.AuthRepository
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDetailsDTO
import com.utn.greenthumb.domain.model.watering.WateringDatesDTO
import com.utn.greenthumb.domain.model.watering.WateringScheduleDTO
import com.utn.greenthumb.utils.RuntimeTypeAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PlantsApiClient {

    private const val CONNECT_TIMEOUT = 30L // segundos
    private const val READ_TIMEOUT = 30L // segundos
    private const val WRITE_TIMEOUT = 30L // segundos

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
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .retryOnConnectionFailure(true)
        .build()

    val typeFactory = RuntimeTypeAdapterFactory
        .of(WateringConfigurationDetailsDTO::class.java, "type")
        .registerSubtype(WateringScheduleDTO::class.java, "schedules")
        .registerSubtype(WateringDatesDTO::class.java, "dates-frequency")

    val gson = GsonBuilder()
        .registerTypeAdapterFactory(typeFactory)
        .create()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PLANT_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: PlantsApiService = retrofit.create(PlantsApiService::class.java)
}
