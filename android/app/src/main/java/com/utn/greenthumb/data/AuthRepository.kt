package com.utn.greenthumb.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun loginWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserName(): String? = firebaseAuth.currentUser?.displayName
}
