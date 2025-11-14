package com.utn.greenthumb.data.repository

import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WateringConfigurationRepository @Inject constructor(
    // We keep the api service dependency even if we don't use it to avoid breaking Hilt's dependency graph.
    private val plantsApi: PlantsApiService
) {

    // In-memory list to act as a mock database for development
    private val inMemoryConfigurations = mutableListOf<WateringConfigurationDTO>()
    private val idCounter = AtomicLong(1)

    suspend fun getConfigurations(): PagedResponse<WateringConfigurationDTO> {
        // Returns the in-memory list instead of making a network call
        return PagedResponse(1,1,1,1,content = inMemoryConfigurations.toList())
    }

    suspend fun create(request: WateringConfigurationDTO): WateringConfigurationDTO {
        // Adds the new configuration to the list and assigns a unique ID
        val newConfigWithId = request.copy(id = idCounter.getAndIncrement().toString())
        inMemoryConfigurations.add(newConfigWithId)
        return newConfigWithId
    }

    suspend fun delete(reminder: WateringConfigurationDTO) {
        // Removes the configuration from the list
        inMemoryConfigurations.removeAll { it.id == reminder.id }
    }

    suspend fun update(request: WateringConfigurationDTO) {
        // Updates a configuration in the list
        val index = inMemoryConfigurations.indexOfFirst { it.id == request.id }
        if (index != -1) {
            inMemoryConfigurations[index] = request
        }
    }
}