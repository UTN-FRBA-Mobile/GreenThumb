package com.utn.greenthumb.data.repository

import android.util.Log
import com.utn.greenthumb.BuildConfig
import com.utn.greenthumb.client.TranslateApiClient
import com.utn.greenthumb.client.services.TranslationApiService
import com.utn.greenthumb.data.model.translator.TranslationRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationRepository @Inject constructor(
    private val api: TranslationApiService
) {

    private val apiKey: String
        get() {
            val key = BuildConfig.GOOGLE_API_KEY
            return key
        }


    /**
     * Traducir texto simple
     */
    suspend fun translateText(text:String): String {
        return try {
            Log.d("TranslationRepository", "Translate: $text")

            if (text.isBlank()) {
                Log.d("TranslationRepository", "Empty text")
                return ""
            }

            val request = TranslationRequest(words=listOf(text))
            val response = api.translate(request, apiKey, "text", "base")

            if (response.data.translations.isEmpty()) {
                throw Exception("Empty response from Google Translation API")
            }

            val translatedText = response.data.translations[0].translatedText
            Log.d("TranslationRepository", "Translated: $translatedText")
            translatedText
        } catch (e: Exception) {
            Log.e("TranslationRepository", "Error translating text: ${e.message}", e)
            throw e
         }
    }
}