package com.utn.greenthumb.manager

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.AuthException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {
    val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    suspend fun signOut() {
        return withContext(Dispatchers.IO) {
            try {
                firebaseAuth.signOut()
                suspendCancellableCoroutine<Unit> { continuation ->
                    googleSignInClient.signOut()
                        .addOnSuccessListener {
                            Log.d("AuthManager", "Google sign out successful")
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AuthManager", "Google sign out failed", exception)
                            continuation.resume(Unit)
                        }
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Error signing out", e)
                throw AuthException(
                    message = "Error al cerrar sesión",
                    cause = e
                )
            }
        }
    }


    suspend fun revokeAccess() {
        return withContext(Dispatchers.IO) {
            try {
                firebaseAuth.signOut()
                suspendCancellableCoroutine<Unit> { continuation ->
                    googleSignInClient.revokeAccess()
                        .addOnSuccessListener {
                            Log.d("AuthManager", "Google access revocation successful")
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AuthManager", "Google access revocation failed", exception)
                            continuation.resume(Unit)
                        }
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Revoke access error", e)
                throw AuthException(
                    message = "Error al revocar acceso",
                    cause = e
                )
            }
        }
    }
}
