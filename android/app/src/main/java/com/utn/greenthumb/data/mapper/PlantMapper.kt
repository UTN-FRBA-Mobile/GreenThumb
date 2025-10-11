package com.utn.greenthumb.data.mapper

import com.utn.greenthumb.data.model.plantid.IdentificationResponse
import com.utn.greenthumb.domain.model.Image
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.domain.model.Taxonomy
import com.utn.greenthumb.domain.model.Watering

object PlantMapper {

    fun fromDto(dto: List<IdentificationResponse>): List<Plant> {
        return dto.map { suggestion ->
            Plant(
                name = suggestion.name,
                probability = suggestion.probability,
                images = suggestion.images?.map { it ->
                    Image(
                        url = it.url
                    )
                } ?: listOf(),
                commonNames = suggestion.commonNames,
                taxonomy = Taxonomy(
                    taxonomyClass = suggestion.taxonomy?.taxonomyClass ?: "N/A",
                    genus = suggestion.taxonomy?.genus ?: "N/A",
                    order = suggestion.taxonomy?.order ?: "N/A",
                    family = suggestion.taxonomy?.family ?: "N/A",
                    phylum = suggestion.taxonomy?.phylum ?: "N/A",
                    kingdom = suggestion.taxonomy?.kingdom ?: "N/A"
                ),
                moreInfoUrl = suggestion.moreInfoUrl,
                description = suggestion.description,
                synonyms = suggestion.synonyms,
                watering = suggestion.watering?.let {
                    Watering(
                        max = it.max,
                        min = it.min
                    )
                },
                id = null
            )
        }
    }
}
