package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plant.PlantRequest
import com.utn.greenthumb.data.model.plant.FavouritePlantRequest
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.UserTokenDTO
import com.utn.greenthumb.domain.model.WateringReminderDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Path

interface PlantsApiService {

    @Headers("Content-Type: application/json")
    @POST("/plants/identify")
    suspend fun identifyPlant(
        @Body request: IdentificationRequest
    ): List<IdentificationResponse>

    @GET("plants/list")
    suspend fun getPlants(
        @Body request: PlantRequest? = null
    ): PagedResponse<PlantDTO>

    @Headers("Content-Type: application/json")
    @POST("/plants")
    suspend fun save(@Body request: PlantDTO)

    @Headers("Content-Type: application/json")
    @POST("/users/notification/token")
    suspend fun updateNotificationToken(@Body request: UserTokenDTO)

    @GET("plants/{plantId}")
    suspend fun getPlant(@Path("plantId") plantId: String): PlantDTO


    @GET("plants/watering-reminders/list")
    suspend fun getWateringReminders(): PagedResponse<WateringReminderDTO>

    @Headers("Content-Type: application/json")
    @POST("/plants/watering-reminders")
    suspend fun saveWateringReminder(
        @Body request: WateringReminderRequest
    )

    @PATCH("/plants/watering-reminders/{id}/check")
    suspend fun checkWateringReminder(
        @Path("id") reminderId: String
    )

    @DELETE("/plants/watering-reminders/{id}")
    suspend fun deleteWateringReminder(
        @Path("id") reminderId: String
    )

    @Headers("Content-Type: application/json")
    @PATCH("/plants/{id}/favourite")
    suspend fun setFavouritePlant(
        @Path("id") reminderId: String,
        @Body request: FavouritePlantRequest
    )
}
