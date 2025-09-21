package com.utn.greenthumb.domain.model

data class Plant(
    val id: String,
    val name: String,
    val probability: Double,
    val similarImages: List<SimilarImage>,
    val commonNames: List<String>,
    val taxonomy: Taxonomy,
    val url: String,
    val rank: String,
    val description: String,
    val synonyms: List<String>,
    val watering: Watering?
)

data class SimilarImage(
    val url: String,
    val similarity: Double
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
