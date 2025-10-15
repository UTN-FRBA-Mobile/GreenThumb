package com.utn.greenthumb.client

import android.util.Log
import com.utn.greenthumb.BuildConfig
import com.utn.greenthumb.client.services.TranslationApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object TranslateApiClient {

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp_Translation", message)
    }.apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30,java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.TRANSLATE_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: TranslationApiService = retrofit.create(TranslationApiService::class.java)

}