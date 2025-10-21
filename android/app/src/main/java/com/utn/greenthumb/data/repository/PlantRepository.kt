package com.utn.greenthumb.data.repository

import android.util.Log
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.PlantDTO
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
        Log.d("PlantRepository", "Response: $response")
        Log.d("PlantRepository", "Response plantResults: ${response.plantResults}")
        Log.d("PlantRepository", "Response isPlant: ${response.isPlant}")

        // Solamente retorna los resultados completos SI es una planta
        // - cuando la probabilidad de que sea una planta es mayor al umbral
        return if (response.isPlant.probability >= response.isPlant.threshold) {
            PlantMapper.fromDto(response.plantResults)
        } else {
            emptyList()
        }
    }

    suspend fun getPlants(): PagedResponse<PlantDTO> {
         return  plantsApi.getPlants()

    }
    suspend fun save(plant: PlantDTO) {
        plantsApi.save(
            request = plant
        )
    }

    suspend fun getPlant(plantId: String): PlantDTO {
        return plantsApi.getPlant(plantId)
    }

}