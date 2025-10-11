package com.utn.greenthumb.domain.model

data class Plant(
    val id: String?,
    val name: String,
    val probability: Double,
    val images: List<Image>,
    val commonNames: List<String>,
    val taxonomy: Taxonomy,
    val moreInfoUrl: String,
    val description: String,
    val synonyms: List<String>,
    val watering: Watering?
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
