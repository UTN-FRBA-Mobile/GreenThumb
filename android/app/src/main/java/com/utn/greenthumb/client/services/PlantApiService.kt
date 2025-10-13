package com.utn.greenthumb.client.services

import com.utn.greenthumb.data.model.plant.PlantResponse
import com.utn.greenthumb.domain.model.Plant
import retrofit2.http.GET
import retrofit2.http.Header

interface PlantApiService {
    @GET("plants/list")
    suspend fun getPlants(@Header("x-client-id") clientId: String): List<Plant>
}