package com.utn.greenthumb.data.model.translator

import com.google.gson.annotations.SerializedName

data class TranslationRequest(
    @SerializedName("q")
    val words: List<String>,
    @SerializedName("source")
    val sourceLanguage: String = "en",
    @SerializedName("target")
    val targetLanguage: String = "es"
)