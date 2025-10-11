package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PlantsApiService {

    @Headers("Content-Type: application/json")
    @POST("/plants/identify")
    suspend fun identifyPlant(
        @Body request: IdentificationRequest
    ): List<IdentificationResponse>

}
