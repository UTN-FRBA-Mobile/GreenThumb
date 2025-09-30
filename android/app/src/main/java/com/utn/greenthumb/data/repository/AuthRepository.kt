package com.utn.greenthumb.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.domain.model.AuthException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    private val _userStateFlow = MutableStateFlow(getCurrentUser())
    val userStateFlow: StateFlow<User?> = _userStateFlow.asStateFlow()


    private val _isUserLoggedInFlow = MutableStateFlow(isUserLoggedIn())
    val isUserLoggedInFlow: StateFlow<Boolean> = _isUserLoggedInFlow.asStateFlow()


    init {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser?.let { firebaseUser ->
                User(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isEmailVerified = firebaseUser.isEmailVerified
                )
            }
            _userStateFlow.value = user
            _isUserLoggedInFlow.value = user != null
            }
    }


    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null


    fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }


    suspend fun loginWithGoogle(idToken: String): User {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()

            val firebaseUser = result.user
                ?: throw AuthException(
                    "Usuario no encontrado después del login",
                    "user_not_found"
                )

            val user = User(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isEmailVerified = firebaseUser.isEmailVerified
            )
            Log.d("AuthRepository", "Login successful for user: ${user.email}")
            user
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "Firebase auth error: ${e.errorCode}", e)
            throw AuthException(
                message = mapFirebaseErrorToMessage(e),
                errorCode = e.errorCode,
                cause = e
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error", e)
            throw AuthException(
                message = "Error en el proceso de autenticación",
                cause = e
            )
        }
    }


    private fun mapFirebaseErrorToMessage(exception: FirebaseAuthException): String {
        return when (exception.errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> "Credenciales de Google inválidas"
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                "Ya existe una cuenta con este email usando un proveedor diferente"
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "Esta cuenta de Google ya está en uso"
            "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada"
            "ERROR_USER_TOKEN_EXPIRED" -> "Tu sesión ha expirado. Inicia sesión nuevamente"
            "ERROR_INVALID_USER_TOKEN" -> "Token de usuario inválido"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Error de conexión. Verifica tu internet"
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta más tarde"
            else -> "Error de autenticación: ${exception.message}"
        }
    }
}


