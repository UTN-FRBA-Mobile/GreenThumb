package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.plant.SetFavouriteRequest
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

    suspend fun getPlant(plantId: String): PlantDTO {
        return plantsApi.getPlant(plantId)
    }

    suspend fun getFavouritePlants(): PagedResponse<PlantDTO> {
        return plantsApi.getPlants(favourites = true)
    }

    suspend fun setFavouritePlant(plantId: String) {
        plantsApi.setFavouritePlant(plantId, SetFavouriteRequest(favourite = true))
    }

    suspend fun unSetFavouritePlant(plantId: String) {
        plantsApi.setFavouritePlant(plantId, SetFavouriteRequest(favourite = false))
    }

}