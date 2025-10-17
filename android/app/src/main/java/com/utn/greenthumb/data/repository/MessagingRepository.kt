package com.utn.greenthumb.data.repository

import android.content.Context
import android.util.Log
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.utils.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val plantsApi: PlantsApiService,
) {

    suspend fun refreshToken(): String? {
        return try {
            Log.d("MessagingRepository", "Refreshing token...")
            val token = NotificationHelper.refreshToken()

            if (token != null) {
                //plantsApi.updateNotificationToken(token)
            }
            token
        } catch (e: Exception) {
            Log.d("MessagingRepository", "Error refreshing token", e)
            null
        }

    }

    suspend fun sendTokenToServer(token:String, userId: String) {
        try {
            Log.d("MessagingRepository", "Sending token to server...")

            Log.d("MessagingRepository", "Token sent to server: ${token.take(20)}")
        } catch (e: Exception) {
            Log.d("MessagingRepository", "Error sending token to server", e)
            throw Exception("Error sending token to server")
        }
    }

}


