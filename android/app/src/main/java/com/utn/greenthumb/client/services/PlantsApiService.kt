package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.UserTokenDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface PlantsApiService {

    @Headers("Content-Type: application/json")
    @POST("/plants/identify")
    suspend fun identifyPlant(
        @Body request: IdentificationRequest
    ): IdentificationResponse

    @GET("plants/list")
    suspend fun getPlants(): PagedResponse<PlantDTO>

    @Headers("Content-Type: application/json")
    @POST("/plants")
    suspend fun save(@Body request: PlantDTO)

    @Headers("Content-Type: application/json")
    @POST("/users/notification/token")
    suspend fun updateNotificationToken(@Body request: UserTokenDTO)

    @GET("plants/{plantId}")
    suspend fun getPlant(@Path("plantId") plantId: String): PlantDTO

    @DELETE("plants/{plantId}")
    suspend fun deletePlant(@Path("plantId") plantId: String)
}
