package com.utn.greenthumb.data.mapper

import android.util.Log
import com.utn.greenthumb.data.model.plant.PagedResponse
import com.utn.greenthumb.data.model.watering.WateringReminderResponse
import com.utn.greenthumb.data.model.watering.WateringReminderRequest
import com.utn.greenthumb.domain.model.WateringDTO
import com.utn.greenthumb.domain.model.WateringReminderDTO


object WateringReminderMapper {

    fun toPagedResponseDto(response: PagedResponse<WateringReminderResponse>): PagedResponse<WateringReminderDTO> {
        Log.d("MAPPER::toPagedResponseDto", response.content.toString())

        return PagedResponse<WateringReminderDTO>(
            page = response.page,
            limit = response.limit,
            total = response.total,
            totalPages = response.totalPages,
            content = toDtoList(response.content)
        )
    }

    fun toDtoList(responses: List<WateringReminderResponse>): List<WateringReminderDTO> {
        return responses.map { response ->
            toDto(response)
        }
    }


    fun toDto(response: WateringReminderResponse): WateringReminderDTO {

        Log.d("MAPPER::toDto", response.toString())
        val watering =
            if (response.watering != null)
                WateringDTO(min = response.watering.min, max = response.watering.max)
            else null

        return WateringReminderDTO(
            id = response.id,
            userId = response.userId,
            plantId = response.plantId,
            plantName = response.plantName,
            plantImageUrl = response.plantImageUrl,
            date = response.date, // El DTO sigue manejando el String ISO 8601
            checked = response.checked,
            watering = watering
        )
    }

    fun fromDtoList(dtos: List<WateringReminderDTO>): List<WateringReminderRequest> {
        return dtos.map { dto ->
            fromDto(dto)
        }
    }

    fun fromDto(dto: WateringReminderDTO): WateringReminderRequest {
        val watering =
            if (dto.watering != null)
                WateringDTO(min = dto.watering.min, max = dto.watering.max)
            else null

        return WateringReminderRequest(
            plantId = dto.plantId,
            plantName = dto.plantName,
            plantImageUrl = dto.plantImageUrl,
            date = dto.date,
            checked = dto.checked,
            watering = watering
        )
    }
}
