package com.utn.greenthumb.data.model.plantid

import com.google.gson.annotations.SerializedName

data class IdentificationResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("result") val result: ResultDTO?
)

data class ResultDTO(
    @SerializedName("classification") val classification: SuggestionsDTO?,
    @SerializedName("is_plant") val isPlant: IsPlantDTO?
)

data class IsPlantDTO(
    @SerializedName("probability") val probability: Double?,
    @SerializedName("threshold") val threshold: Double?,
    @SerializedName("binary") val binary: Boolean?
)

data class SuggestionsDTO(
    @SerializedName("suggestions") val suggestions: List<SuggestionDTO>?
)

data class SuggestionDTO(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("probability") val probability: Double?,
    @SerializedName("similar_images") val similarImages: List<SimilarImageDTO>?,
    @SerializedName("details") val details: DetailsDTO?
)

data class SimilarImageDTO(
    @SerializedName("url") val url: String?,
    @SerializedName("similarity") val similarity: Double?
)

data class DetailsDTO(
    @SerializedName("common_names") val commonNames: List<String>?,
    @SerializedName("taxonomy") val taxonomy: TaxonomyDTO?,
    @SerializedName("url") val url: String?,
    @SerializedName("rank") val rank: String?,
    @SerializedName("description") val description: DescriptionDTO?,
    @SerializedName("synonyms") val synonyms: List<String>?,
    @SerializedName("watering") val watering: WateringDTO?
)

data class TaxonomyDTO(
    @SerializedName("class") val taxonomyClass: String?,
    @SerializedName("genus") val genus: String?,
    @SerializedName("order") val order: String?,
    @SerializedName("family") val family: String?,
    @SerializedName("phylum") val phylum: String?,
    @SerializedName("kingdom") val kingdom: String?
)

data class DescriptionDTO(
    @SerializedName("value") val value: String?,
    @SerializedName("citation") val citation: String?
)

data class WateringDTO(
    @SerializedName("max") val max: Int?,
    @SerializedName("min") val min: Int?
)