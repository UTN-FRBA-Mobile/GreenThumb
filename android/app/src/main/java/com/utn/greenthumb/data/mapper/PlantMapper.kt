package com.utn.greenthumb.data.mapper

import com.utn.greenthumb.data.model.plantid.PlantResults
import com.utn.greenthumb.domain.model.ImageDTO
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.TaxonomyDTO
import com.utn.greenthumb.domain.model.WateringDTO

object PlantMapper {

    fun fromDto(dto: List<PlantResults>): List<PlantDTO> {
        return dto.map { suggestion ->
            PlantDTO(
                id = null,
                externalId = suggestion.externalId,
                name = suggestion.name,
                probability = suggestion.probability,
                images = suggestion.images?.map { it ->
                    ImageDTO(
                        url = it.url
                    )
                } ?: listOf(),
                commonNames = suggestion.commonNames,
                synonyms = suggestion.synonyms,
                commonUses = suggestion.commonUses,
                description = suggestion.description,
                culturalSignificance = suggestion.culturalSignificance,
                taxonomy = TaxonomyDTO(
                    taxonomyClass = suggestion.taxonomy?.taxonomyClass ?: "N/A",
                    genus = suggestion.taxonomy?.genus ?: "N/A",
                    order = suggestion.taxonomy?.order ?: "N/A",
                    family = suggestion.taxonomy?.family ?: "N/A",
                    phylum = suggestion.taxonomy?.phylum ?: "N/A",
                    kingdom = suggestion.taxonomy?.kingdom ?: "N/A"
                ),
                toxicity = suggestion.toxicity,
                moreInfoUrl = suggestion.moreInfoUrl,
                watering = suggestion.watering.let {
                    WateringDTO(
                        max = it?.max ?: 0,
                        min = it?.min ?: 0
                    )
                },
                bestWatering = suggestion.bestWatering,
                propagationMethods = suggestion.propagationMethods,
                bestLightCondition = suggestion.bestLightCondition
            )
        }
    }
}
