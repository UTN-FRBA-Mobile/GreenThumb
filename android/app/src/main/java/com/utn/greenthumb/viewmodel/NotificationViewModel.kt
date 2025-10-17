package com.utn.greenthumb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: MessagingRepository
) : ViewModel() {

    var token by mutableStateOf<String?>(null)
        private set

    fun refreshToken() {
        viewModelScope.launch {
            token = repository.refreshToken()
        }
    }
}