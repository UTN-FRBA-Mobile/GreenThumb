package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface PlantIdApiService {

    @Headers("Content-Type: application/json")
    @POST("identification")
    suspend fun identifyPlant(
        @Query("details") details: List<String>,
        @Query("language") language: String,
        @Body request: IdentificationRequest
    ): IdentificationResponse

}
