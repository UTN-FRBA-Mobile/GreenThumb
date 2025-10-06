package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantIdApiService
import com.utn.greenthumb.data.mapper.PlantMapper
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.utils.MockResponse
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val api: PlantIdApiService
) {
    suspend fun identifyPlant(
        request: IdentificationRequest
    ): List<Plant> {

        val dto = MockResponse.mockIdentificationResponse()
        // TODO: Solamente para mockear esto se comenta
//        val response = api.identifyPlant(
//            details = DEFAULT_DETAILS,
//            language = DEFAULT_LANGUAGE,
//            request = request)

        return PlantMapper.fromDto(dto)
    }

    companion object {
        private val DEFAULT_DETAILS = listOf(
            "common_names",
            "scientific_name",
            "synonyms",
            "url",
            "description",
            "taxonomy",
            "rank",
            "watering")

        private const val DEFAULT_LANGUAGE = "es"

    }
}