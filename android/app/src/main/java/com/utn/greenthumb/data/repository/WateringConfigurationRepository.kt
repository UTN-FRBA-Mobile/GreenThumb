package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WateringConfigurationRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {

    suspend fun getConfigurations(): PagedResponse<WateringConfigurationDTO> {
        return plantsApi.getWateringConfigurations()
    }

    suspend fun create(request: WateringConfigurationDTO) {
        plantsApi.createWateringConfiguration(request)
    }

    suspend fun delete(reminder: WateringConfigurationDTO) {
        plantsApi.deleteWateringConfiguration(reminder.id ?: "")
    }

    suspend fun update(request: WateringConfigurationDTO) {
        plantsApi.editWateringConfiguration(request.id ?: "", request)
    }
}