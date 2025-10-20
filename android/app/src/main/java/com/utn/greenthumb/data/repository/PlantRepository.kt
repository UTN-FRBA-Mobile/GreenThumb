package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plant.PlantRequest
import com.utn.greenthumb.data.model.plant.FavouritePlantRequest
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.WateringReminderDTO
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {
    suspend fun identifyPlant(
        request: IdentificationRequest
    ): List<PlantDTO> {

        val response = plantsApi.identifyPlant(
            request = request
        )

        return PlantMapper.fromDto(response)
    }

    suspend fun getPlants(): PagedResponse<PlantDTO> {
         return  plantsApi.getPlants()

    }
    suspend fun save(plant: PlantDTO) {
        plantsApi.save(
            request = plant
        )
    }

    suspend fun getFavouritePlants(): PagedResponse<PlantDTO> {
        return plantsApi.getPlants(PlantRequest(favourite = true))
    }

    suspend fun getWateringReminders(): PagedResponse<WateringReminderDTO> {
        return plantsApi.getWateringReminders()
    }

    suspend fun checkWateringSchedule(reminderId: String) {
        plantsApi.checkWateringReminder(reminderId)
    }

    suspend fun saveWateringReminder(reminder: WateringReminderDTO) {
        val request = WateringReminderRequest(
            plantId = reminder.plantId,
            plantName = reminder.plantName,
            plantImageUrl = reminder.plantImageUrl,
            date = reminder.date,
            checked = false
        )
        plantsApi.saveWateringReminder(request)
    }

    suspend fun deleteWateringReminder(reminderId: String) {
        plantsApi.deleteWateringReminder(reminderId)
    }

    suspend fun setFavouritePlant(plantId: String) {
        plantsApi.setFavouritePlant(plantId, FavouritePlantRequest(favourite = true))
    }

    suspend fun unSetFavouritePlant(plantId: String) {
        plantsApi.setFavouritePlant(plantId, FavouritePlantRequest(favourite = false))
    }

}