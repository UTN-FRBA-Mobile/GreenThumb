package com.utn.greenthumb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun loginWithGoogleToken(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            authRepository.loginWithGoogle(
                idToken = idToken,
                onSuccess = { onSuccess() },
                onError = { onError(it) }
            )
        }
    }

    fun logout() = authRepository.logout()

    fun getUserName(): String? = authRepository.getCurrentUserName()
}
