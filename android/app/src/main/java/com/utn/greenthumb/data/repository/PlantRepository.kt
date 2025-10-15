package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.Plant
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {
    suspend fun identifyPlant(
        request: IdentificationRequest
    ): List<Plant> {

        val response = plantsApi.identifyPlant(
            request = request
        )

        return PlantMapper.fromDto(response)
    }

    suspend fun getPlants(clientId: String): PagedResponse<Plant> {
         return  plantsApi.getPlants(clientId)

    }
    suspend fun save(plant: Plant) {
        plantsApi.save(request = plant)
    }

}