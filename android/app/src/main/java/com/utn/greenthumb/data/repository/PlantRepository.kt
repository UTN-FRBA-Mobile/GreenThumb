package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.Plant
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val api: PlantsApiService
) {
    suspend fun identifyPlant(
        request: IdentificationRequest
    ): List<Plant> {

        val response = api.identifyPlant(
            request = request
        )

        return PlantMapper.fromDto(response)
    }

}