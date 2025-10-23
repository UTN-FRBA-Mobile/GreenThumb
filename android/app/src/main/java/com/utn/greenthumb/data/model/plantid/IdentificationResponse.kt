package com.utn.greenthumb.data.model.plantid

import com.google.gson.annotations.SerializedName

data class IdentificationResponse(
    @SerializedName("plant_results") val plantResults: List<PlantResults>,
    @SerializedName("is_plant") val isPlant: IsPlant
)

data class PlantResults(
    @SerializedName("_id") val id: String,
    val externalId: String,
    val name: String,
    val probability: Double,
    val images: List<Image>?,
    val commonNames: List<String>,
    val taxonomy: Taxonomy?,
    val moreInfoUrl: String?,
    val description: String,
    val synonyms: List<String>?,
    val watering: Watering?,
    val bestWatering: String?,
    val propagationMethods: List<String>?,
    val culturalSignificance: String?,
    val bestLightCondition: String?,
    val commonUses: String?,
    val toxicity: String?
)
data class Image(
    val url: String
)

data class Taxonomy(
    val taxonomyClass: String,
    val genus: String,
    val order: String,
    val family: String,
    val phylum: String,
    val kingdom: String
)

data class Watering(
    val max: Int,
    val min: Int
)

data class IsPlant(
    val probability: Double,
    val threshold: Double,
    val binary: Boolean
)