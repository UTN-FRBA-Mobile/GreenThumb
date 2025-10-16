package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import com.utn.greenthumb.domain.model.Plant
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface PlantsApiService {

    @Headers("Content-Type: application/json")
    @POST("/plants/identify")
    suspend fun identifyPlant(
        @Body request: IdentificationRequest
    ): List<IdentificationResponse>

    @GET("plants/list")
    suspend fun getPlants(): PagedResponse<Plant>

    @Headers("Content-Type: application/json")
    @POST("/plants")
    suspend fun save(@Body request: Plant)

    @Headers("Content-Type: application/json")
    @PUT("/users/notification")
    suspend fun updateNotificationToken(token: String)
}
