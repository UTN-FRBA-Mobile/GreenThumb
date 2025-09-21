package com.utn.greenthumb.data.mapper

import com.utn.greenthumb.data.model.plantid.*
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.domain.model.SimilarImage
import com.utn.greenthumb.domain.model.Taxonomy
import com.utn.greenthumb.domain.model.Watering

object PlantMapper {

    fun fromDto(dto: IdentificationResponse): List<Plant> {
        val suggestions = dto.result?.classification?.suggestions ?: return emptyList()

        return suggestions.map { suggestion ->
            Plant(
                id = suggestion.id ?: "Desconocido",
                name = suggestion.name ?: "Planta desconocida",
                probability = suggestion.probability ?: 0.0,
                similarImages = suggestion.similarImages?.map { similarImage ->
                    SimilarImage(
                        url = similarImage.url ?: "",
                        similarity = similarImage.similarity ?: 0.0
                    )
                } ?: emptyList(),
                commonNames = suggestion.details?.commonNames ?: emptyList(),
                taxonomy = Taxonomy(
                    taxonomyClass = suggestion.details?.taxonomy?.taxonomyClass ?: "N/A",
                    genus = suggestion.details?.taxonomy?.genus ?: "N/A",
                    order = suggestion.details?.taxonomy?.order ?: "N/A",
                    family = suggestion.details?.taxonomy?.family ?: "N/A",
                    phylum = suggestion.details?.taxonomy?.phylum ?: "N/A",
                    kingdom = suggestion.details?.taxonomy?.kingdom ?: "N/A"
                ),
                url = suggestion.details?.url ?: "N/A",
                rank = suggestion.details?.rank ?: "N/A",
                description = suggestion.details?.description?.value ?: "",
                synonyms = suggestion.details?.synonyms ?: emptyList(),
                watering = suggestion.details?.watering?.let {
                    Watering(
                        max = it.max ?: 0,
                        min = it.min ?: 0
                    )
                }
            )
        }
    }
}
