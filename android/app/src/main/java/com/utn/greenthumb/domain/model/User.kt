package com.utn.greenthumb.domain.model

data class User(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)

data class UserTokenDTO(
    val token: String
)