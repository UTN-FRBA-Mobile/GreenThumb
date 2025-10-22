package com.utn.greenthumb.data.model.plant

import com.google.gson.annotations.SerializedName

data class SetFavouriteRequest(
    @SerializedName("favourite") val favourite: Boolean
)