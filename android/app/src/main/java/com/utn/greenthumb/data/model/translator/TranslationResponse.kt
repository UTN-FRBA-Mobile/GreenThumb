package com.utn.greenthumb.data.model.translator

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
    @SerializedName("data")
    val data: Data
)

data class Data(
    @SerializedName("translations")
    val translations: List<TranslatedText>
)