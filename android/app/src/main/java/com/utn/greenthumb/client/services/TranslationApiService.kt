package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.translator.TranslationRequest
import com.utn.greenthumb.data.model.translator.TranslationResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslationApiService {

    @POST("language/translate/v2")
    suspend fun translate(
        @Body request: TranslationRequest,
        @Query("key") apiKey: String,
        @Query("format") format: String,
        @Query("model") model: String
    ) : TranslationResponse
}