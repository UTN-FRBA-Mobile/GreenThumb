package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.AuthRepository
import com.utn.greenthumb.domain.model.AuthException
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.manager.AuthManager
import com.utn.greenthumb.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : BaseViewModel<User>() {

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedInFlow
    val currentUser: StateFlow<User?> = authRepository.userStateFlow

    private val _logoutState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val logoutState: StateFlow<UiState<Unit>> = _logoutState.asStateFlow()

    private val _revokeState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val revokeState: StateFlow<UiState<Unit>> = _revokeState.asStateFlow()

    init {
        // Sincronizar estado inicial con el repository
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                setSuccess(currentUser)
            } else {
                setIdle()
            }
        }
    }

    /**
     * Login con Google Token
     */
    fun loginWithGoogleToken(
        idToken: String
    ) {
        executeOperation(
            operation = {
                authRepository.loginWithGoogle(idToken)
            },
            onError = { throwable ->
                when (throwable) {
                    is AuthException -> throwable.message ?: "Error de autenticación"
                    is java.net.UnknownHostException -> "Sin conexión a internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> "Error inesperado en el login"
                }
            }
        )
    }

    /**
     * Logout del usuario actual
     */
    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.value = UiState.Loading
                authManager.signOut()
                _logoutState.value = UiState.Success(Unit)
                setIdle() // Reset login state
                Log.d("AuthViewModel", "Logout successful")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout failed", e)
                _logoutState.value = UiState.Error(
                    message = "Error al cerrar sesión",
                    throwable = e
                )
            }
        }
    }

    /**
     * Revocar acceso del usuario
     */
    fun revokeAccess() {
        viewModelScope.launch {
            try {
                _revokeState.value = UiState.Loading
                authManager.revokeAccess()
                _revokeState.value = UiState.Success(Unit)
                setIdle() // Reset login state
                Log.d("AuthViewModel", "Access revocation successful")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Access revocation failed", e)
                _revokeState.value = UiState.Error(
                    message = "Error al revocar acceso",
                    throwable = e
                )
            }
        }
    }

    /**
     * Obtener el nombre de usuario actual
     */
    fun getUserName(): String? = authRepository.getCurrentUser()?.displayName

    /**
     * Obtener el email de usuario actual
     */
    fun getUserEmail(): String? = authRepository.getCurrentUser()?.email

    /**
     * Obtener el avatar de usuario actual
     */
    fun getUserAvatar(): String? = authRepository.getCurrentUser()?.photoUrl

    /**
     * Obtener cliente de Google Sign-In
     */
    fun getGoogleSignInClient() = authManager.googleSignInClient

    /**
     * Reintentar última operación
     */
    fun retryLogin() {
        clearError()
    }

    /**
     * Limpiar todos los estados de error
     */
    fun clearAllErrors() {
        clearError()
        _logoutState.value = UiState.Idle
        _revokeState.value = UiState.Idle
    }
}
