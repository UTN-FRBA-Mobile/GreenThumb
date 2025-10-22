package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plant.SetFavouriteRequest
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.domain.model.WateringReminderDTO
import javax.inject.Inject

class WateringReminderRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {

    suspend fun getWateringReminders(): PagedResponse<WateringReminderDTO> {
        return plantsApi.getWateringReminders()
    }

    suspend fun checkWateringReminder(reminderId: String) {
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

}