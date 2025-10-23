package com.utn.greenthumb.data.repository

import android.util.Log
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.PlantDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val plantsApi: PlantsApiService
) {

    /**
     * Identifica una planta mediante una foto
     */
    suspend fun identifyPlant(
        request: IdentificationRequest
    ): List<PlantDTO> {

        try {
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
        } catch (e: Exception) {
            Log.e("PlantRepository", "Error identifying plant", e)
            throw e
        }
    }


    /**
     * Obtiene todas las plantas del usuario
     */
    suspend fun getPlants(): PagedResponse<PlantDTO> = withContext(Dispatchers.IO) {
         try {
             Log.d("PlantRepository", "Fetching plants...")
             val response = plantsApi.getPlants()
             Log.d("PlantRepository", "Successfully fetched ${response.total} plants")
             Log.d("PlantRepository", "Plants fetched ${response.content}")
             response
         } catch (e: Exception) {
             Log.e("PlantRepository", "Error fetching plants", e)
             throw e
         }
    }


    /**
     * Guarda una planta
     */
    suspend fun save(plant: PlantDTO) = withContext(Dispatchers.IO) {
        try {
            Log.d("PlantRepository", "Saving plant: ${plant.name}")
            plantsApi.save(request = plant)
            Log.d("PlantRepository", "Plant saved successfully")
        } catch (e: Exception) {
            Log.d("PlantRepository", "Error saving plant", e)
            throw e
        }
    }


    /**
     * Obtiene una planta específica por ID
     */
    suspend fun getPlant(plantId: String): PlantDTO = withContext(Dispatchers.IO) {
        try {
            Log.d("PlantRepository", "Fetching plant with ID: $plantId")
            val plant = plantsApi.getPlant(plantId)
            Log.d("PlantRepository", "Plant fetched successfully: ${plant.name}")
            plant
        } catch (e: Exception) {
            Log.e("PlantRepository", "Error fetching plant with ID: $plantId", e)
            throw e
        }
    }


    /**
     * Elimina una planta por ID
     */
    suspend fun deletePlant(plantId: String) = withContext(Dispatchers.IO) {
        try {
            Log.d("PlantRepository", "Deleting plant with ID: $plantId")
            plantsApi.deletePlant(plantId)
            Log.d("PlantRepository", "Plant deleted successfully")
        } catch (e: Exception) {
            Log.e("PlantRepository", "Error deleting plant with ID: $plantId", e)
            throw e
        }
    }

}