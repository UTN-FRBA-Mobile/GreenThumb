package com.utn.greenthumb.data.model.plant

import com.google.gson.annotations.SerializedName

data class FavouritePlantRequest(
    @SerializedName("favourite") val favourite: Boolean
)