package com.utn.greenthumb.data.services

import android.util.Log
import com.utn.greenthumb.data.repository.TranslationRepository
import com.utn.greenthumb.domain.model.PlantDTO
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantTranslationService @Inject constructor(
    private val translationRepository: TranslationRepository
) {

    suspend fun translatePlant(plant: PlantDTO) : PlantDTO {
        return try {
            Log.d("PlantTranslationService", "Starting translation for plant: ${plant.name}")

            val textsToTranslate = buildList {

                // Description
                plant.description.takeIf { it.isNotBlank() }?.let { add(it) }

                // Best watering
                plant.bestWatering?.takeIf { it.isNotBlank() }?.let { add(it) }

                // Best light condition
                plant.bestLightCondition?.takeIf { it.isNotBlank() }?.let { add(it) }

                // Common uses
                plant.commonUses?.takeIf { it.isNotBlank() }?.let { add(it) }

                // Cultural significance
                plant.culturalSignificance?.takeIf { it.isNotBlank() }?.let { add(it) }

                // Toxicity
                plant.toxicity?.takeIf { it.isNotBlank() }?.let { add(it) }

                // Propagation methods
                plant.propagationMethods?.forEach { method ->
                    if (method.isNotBlank()) add(method)
                }
            }

            if (textsToTranslate.isEmpty()) {
                Log.d("PlantTranslationService", "No texts to translate for plant: ${plant.name}")
                return plant
            }

            Log.d("PlantTranslationService", "Translating ${textsToTranslate.size} texts for plant: ${plant.name}")

            val translatedTexts = translationRepository.translateTextList(textsToTranslate)

            var translationIndex = 0

            val translatedPlant = plant.copy(
                description = if (plant.description.isNotBlank()) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.description
                },

                bestWatering = if (plant.bestWatering?.isNotBlank() == true) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.bestWatering
                },

                bestLightCondition = if (plant.bestLightCondition?.isNotBlank() == true) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.bestLightCondition
                },

                commonUses = if (plant.commonUses?.isNotBlank() == true) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.commonUses
                },

                culturalSignificance = if (plant.culturalSignificance?.isNotBlank() == true) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.culturalSignificance
                },

                toxicity = if (plant.toxicity?.isNotBlank() == true) {
                    translatedTexts[translationIndex++]
                } else {
                    plant.toxicity
                },

                propagationMethods = plant.propagationMethods?.map { method ->
                    if (method.isNotBlank()) {
                        translatedTexts[translationIndex++]
                    } else {
                        method
                    }
                }
            )

            Log.d("PlantTranslationService", "Translated plant: $translatedPlant")
            translatedPlant
        } catch (e: Exception) {
            Log.e("PlantTranslationService", "Error translating plant, saving original version.", e)
            return plant
        }
    }
}
