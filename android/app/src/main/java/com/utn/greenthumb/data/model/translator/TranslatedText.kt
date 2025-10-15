package com.utn.greenthumb.data.model.translator

import com.google.gson.annotations.SerializedName

data class TranslatedText(
    @SerializedName("translatedText")
    val translatedText: String
)