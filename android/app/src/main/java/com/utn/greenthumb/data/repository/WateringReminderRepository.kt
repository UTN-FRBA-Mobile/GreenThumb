package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.domain.model.WateringReminderDTO
import com.utn.greenthumb.data.mapper.WateringReminderMapper
import javax.inject.Inject
import android.util.Log
import java.io.IOException

class WateringReminderRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {
    private val TAG = "WateringReminderRepository"

    suspend fun getWateringReminders(): PagedResponse<WateringReminderDTO> {
        try {
            val response = plantsApi.getWateringReminders()
            return WateringReminderMapper.toPagedResponseDto(response)
        }
        catch (e: Exception) {
            Log.d(TAG, "Error getting watering reminders", e)
            throw e
        }
    }

    suspend fun checkWateringReminder(reminderId: String) {
        try {
            plantsApi.checkWateringReminder(reminderId)
        }
        catch (e: Exception) {
            Log.d(TAG, "Error checking watering reminder with ID $reminderId", e)
            throw e
        }
    }

    suspend fun saveWateringReminder(reminder: WateringReminderDTO) {
        try {
            val request = WateringReminderRequest(
                plantId = reminder.plantId,
                plantName = reminder.plantName,
                plantImageUrl = reminder.plantImageUrl,
                date = reminder.date,
                checked = false,
                watering = reminder.watering
            )
            plantsApi.saveWateringReminder(request)
        }
        catch (e: Exception) {
            Log.d(TAG, "Error saving watering reminder", e)
            throw e
        }
    }

    suspend fun deleteWateringReminder(reminderId: String) {
        try {
            plantsApi.deleteWateringReminder(reminderId)
        }
        catch (e: Exception) {
            Log.d(TAG, "Error deleting watering reminder with ID $reminderId", e)
            throw e
        }
    }

}