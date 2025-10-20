package com.utn.greenthumb.data.model.plantid

import com.google.gson.annotations.SerializedName
data class IdentificationRequest(
    @SerializedName("images") val images: List<String>,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
)

