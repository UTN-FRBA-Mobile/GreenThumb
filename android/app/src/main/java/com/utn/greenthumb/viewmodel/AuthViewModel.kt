package com.utn.greenthumb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.AuthRepository
import com.utn.greenthumb.manager.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : ViewModel() {

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun loginWithGoogleToken(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it as Exception) }
        }
    }

    fun logout(onComplete: () -> Unit) {
        authManager.signOut { onComplete() }
    }

    fun revokeAccess(onComplete: () -> Unit) {
        authManager.revokeAccess { onComplete() }
    }

    fun getUserName(): String? = authRepository.getCurrentUserName()

    fun getGoogleSignInClient() = authManager.googleSignInClient
}
