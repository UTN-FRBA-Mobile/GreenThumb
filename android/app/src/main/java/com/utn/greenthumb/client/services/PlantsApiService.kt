package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plant.SetFavouriteRequest
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.data.model.watering.WateringReminderResponse
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
import retrofit2.http.Query

interface PlantsApiService {

    @Headers("Content-Type: application/json")
    @POST("/plants/identify")
    suspend fun identifyPlant(
        @Body request: IdentificationRequest
    ): IdentificationResponse

    @GET("plants/list")
    suspend fun getPlants(
        @Query("favourites") favourites: Boolean? = null
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
    suspend fun getWateringReminders(): PagedResponse<WateringReminderResponse>

    @Headers("Content-Type: application/json")
    @POST("/plants/watering-reminders")
    suspend fun saveWateringReminder(
        @Body request: WateringReminderRequest
    )

    @PATCH("/plants/watering-reminders/{reminderId}/check")
    suspend fun checkWateringReminder(
        @Path("reminderId") reminderId: String
    )

    @GET("plants/watering-reminders/{reminderId}")
    suspend fun getWateringReminder(@Path("reminderId") reminderId: String): WateringReminderDTO

    @DELETE("/plants/watering-reminders/{reminderId}")
    suspend fun deleteWateringReminder(
        @Path("reminderId") reminderId: String
    )

    @Headers("Content-Type: application/json")
    @PATCH("/plants/{plantId}/favourite")
    suspend fun setFavouritePlant(
        @Path("plantId") plantId: String,
        @Body request: SetFavouriteRequest
    )
}
