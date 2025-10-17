package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: MessagingRepository
) : ViewModel() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    fun refreshToken() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("NotificationViewModel", "Getting token...")
                val newToken = repository.refreshToken()

                if (newToken != null) {
                    _token.value = newToken
                    Log.d("NotificationViewModel", "Token updated: ${newToken.take(20)}")
                } else {
                    _error.value = "Error getting token"
                    Log.w("NotificationViewModel", "Token is null")
                }
            } catch (e: Exception) {
                Log.d("NotificationViewModel", "Error refreshing token", e)
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshAndSendToken(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("NotificationViewModel", "Getting token...")
                val newToken = repository.refreshToken()

                if (newToken != null) {
                    _token.value = newToken
                    repository.sendTokenToServer(newToken, userId)
                    Log.d("NotificationViewModel", "Token updated: ${newToken.take(20)}")
                } else {
                    _error.value = "Error getting token"
                    Log.w("NotificationViewModel", "Token is null")
                }
            } catch (e: Exception) {
                Log.d("NotificationViewModel", "Error refreshing token", e)
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}