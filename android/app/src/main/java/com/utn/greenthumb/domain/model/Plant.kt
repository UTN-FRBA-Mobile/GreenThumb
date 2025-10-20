package com.utn.greenthumb.domain.model

import java.io.Serializable

data class PlantDTO(
    val id: String?,
    val externalId: String,
    val name: String,
    val probability: Double,
    val images: List<ImageDTO>?,
    val commonNames: List<String>,
    val taxonomy: TaxonomyDTO?,
    val moreInfoUrl: String?,
    val description: String,
    val synonyms: List<String>?,
    val watering: WateringDTO?,
    val bestWatering: String?,
    val propagationMethods: List<String>?,
    val culturalSignificance: String?,
    val bestLightCondition: String?,
    val commonUses: String?,
    val toxicity: String?
): Serializable

data class ImageDTO(
    val url: String
): Serializable

data class TaxonomyDTO(
    val taxonomyClass: String?,
    val genus: String?,
    val order: String?,
    val family: String?,
    val phylum: String?,
    val kingdom: String?
): Serializable

data class WateringDTO(
    val max: Int,
    val min: Int
): Serializable
