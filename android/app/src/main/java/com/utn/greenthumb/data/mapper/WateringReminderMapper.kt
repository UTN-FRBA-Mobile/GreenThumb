package com.utn.greenthumb.data.mapper

import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import com.utn.greenthumb.domain.model.WateringReminderDTO
import com.utn.greenthumb.data.model.watering.WateringReminderResponse


object WateringReminderMapper {

    fun fromDto(dto: List<WateringReminderResponse>): List<WateringReminderDTO> {

        return dto.map { suggestion ->
            WateringReminderDTO (
                id = suggestion.id,
                plantId = suggestion.plantId,
                plantName = suggestion.plantName,
                plantImageUrl = suggestion.plantImageUrl,
                date = suggestion.date,
                checked = suggestion.checked
            )
        }
    }
}
