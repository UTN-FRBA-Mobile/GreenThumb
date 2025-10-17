package com.utn.greenthumb.data.repository

import android.util.Log
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.domain.model.UserTokenDTO
import com.utn.greenthumb.utils.NotificationHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    private val plantsApi: PlantsApiService,
) {

    suspend fun refreshToken(): String? {
        return try {
            Log.d("MessagingRepository", "Refreshing token...")
            val token = NotificationHelper.refreshToken()

            if (token != null) {
                plantsApi.updateNotificationToken(UserTokenDTO(token))
            }
            token
        } catch (e: Exception) {
            Log.d("MessagingRepository", "Error refreshing token", e)
            null
        }

    }

}


